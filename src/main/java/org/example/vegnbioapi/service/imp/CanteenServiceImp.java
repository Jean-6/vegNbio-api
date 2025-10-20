package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.CanteenDto;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.UserRepo;
import org.example.vegnbioapi.service.CanteenService;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CanteenServiceImp implements CanteenService {
    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StorageService storageService;




    @Override
    public Canteen saveCanteen(CanteenDto canteenDto, List<MultipartFile> pictures) throws IOException {
        List<String> imgUrls = new ArrayList<>();

        for(int index = 0; index< pictures.size(); index++){
            String filename = "img_"+ index + "."+Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = "canteen/" + Utils.generateFolderName() + "/"+filename ;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(pictures.get(index).getContentType())
                            .build(),
                    RequestBody.fromBytes(pictures.get(index).getBytes()));
            imgUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);
        }

        Canteen canteen =  new Canteen();
        canteen.setName(canteenDto.getName());
        canteen.setDesc(canteenDto.getDesc());
        canteen.setEquipments(canteenDto.getEquipments());
        //canteen.setSeats(canteenDto.getSeats());
        canteen.setMeetingRooms(canteenDto.getMeetingRooms());
        canteen.setOpeningHoursMap(canteenDto.getOpeningHoursMap());
        canteen.setLocation(canteenDto.getLocation());
        canteen.setContact(canteenDto.getContact());
        canteen.setTags(canteenDto.getTags());
        canteen.setMenuIds(canteenDto.getMenuIds());
        canteen.setPictures(imgUrls);
        return canteenRepo.save(canteen);
    }

    @Override
    public List<Canteen> getCanteenForCurrentUser(Principal principal, CanteenFilter filters) {


        User user = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(()-> new ResourceNotFoundException(""));

        Criteria criteria = new Criteria();

        if (filters.getCanteenName() != null && !filters.getCanteenName().isEmpty()) criteria.and("name").regex(filters.getCanteenName(), "i");
        if (filters.getCity() != null && !filters.getCity().isEmpty()) criteria.and("canteenId").is(filters.getCity());


        if (user.getId() != null && !user.getId().isEmpty()) criteria.and("userId").is(user.getId());

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Canteen.class);

    }



    @Override
    public List<Canteen> loadFilteredCanteens(CanteenFilter filters) {

       List<AggregationOperation> pipeline = new ArrayList<>();

       pipeline.add(Aggregation.lookup("menu","menuIds","_id","menu"));

       pipeline.add(Aggregation.unwind("menu",true));

       pipeline.add(Aggregation.unwind("menu.dishes", true));

       List<Criteria> criteriaList = new ArrayList<>();

       if(filters.getCanteenName() != null && !filters.getCanteenName().isEmpty()){
           criteriaList.add(Criteria.where("name").regex(filters.getCanteenName(),"i"));
       }

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        return  mongoTemplate.aggregate(aggregation,"canteen",Canteen.class).getMappedResults();


    }

    @Override
    public Canteen loadById(String id) {

        return canteenRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Canteen not found with id:"+id));
    }

    @Override
    public Canteen delete(String id) {
        Optional<Canteen> canteenOpt= canteenRepo.findById(id);
        if (canteenOpt.isEmpty()) {
            throw new ResourceNotFoundException("Menu item not found with id : " + id);
        }
        Canteen canteen = canteenOpt.get();
        storageService.deleteFromS3(canteen.getPictures());
        canteenRepo.deleteById(canteen.getId());
        return  canteen;
    }


}

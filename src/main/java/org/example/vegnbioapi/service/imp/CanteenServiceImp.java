package org.example.vegnbioapi.service.imp;

import com.mongodb.internal.operation.AggregateOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.CanteenDto;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.dto.OfferFilter;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.Offer;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        canteen.setSeats(canteenDto.getSeats());
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
    public Canteen delete(String id) {

        Query query = new Query(Criteria.where("id").is(id));
        Canteen canteenDeleted =  mongoTemplate.findAndRemove(query, Canteen.class);

        assert canteenDeleted != null;
        log.info("deleted : "+ canteenDeleted.getId());

        return canteenDeleted;
    }


}

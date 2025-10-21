package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddCanteen;
import org.example.vegnbioapi.dto.ApprovalRequest;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.model.Approval;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.Status;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.UserRepo;
import org.example.vegnbioapi.service.CanteenService;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CanteenServiceImp implements CanteenService {
    @Autowired
    private CanteenRepo canteenRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StorageService storageService;




    @Override
    public Canteen saveCanteen(AddCanteen canteenDto, List<MultipartFile> pictures) throws IOException {


        log.info("canteenDto : "+ canteenDto.toString());

        Canteen canteen =  new Canteen();
        canteen.setName(canteenDto.getName());
        canteen.setDesc(canteenDto.getDesc());
        canteen.setEquipments(canteenDto.getEquipments());
        canteen.setMeetingRooms(canteenDto.getMeetingRooms());
        canteen.setOpeningHoursMap(canteenDto.getOpeningHoursMap());
        canteen.setLocation(canteenDto.getLocation());
        canteen.setContact(canteenDto.getContact());
        canteen.setTags(canteenDto.getTags());
        canteen.setUserId(canteenDto.getUserId());

        Approval app= new Approval();
        app.setStatus(Status.PENDING);
        canteen.setApproval(app);
        canteen.setApproval(app);

        canteen.setCreatedAt(LocalDateTime.now());
        List<String> picturesUrl = this.storageService.savePictures(pictures);
        canteen.setPictures(picturesUrl);
        return canteenRepo.save(canteen);
    }

    @Override
    public List<Canteen> getApprovedCanteensForCurrentUser(Principal principal) {
        User user = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Criteria criteria = new Criteria();
        if (user.getId() != null && !user.getId().isEmpty()) {
            criteria.and("userId").is(user.getId());
        }
        criteria.and("approval.status").is(Status.APPROVED);

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Canteen.class);
    }

    @Override
    public List<Canteen> getCanteenForCurrentUser(Principal principal, CanteenFilter filters) {


        User user = userRepo.findUserByUsername(principal.getName())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with name:"+ principal.getName()));

        Criteria criteria = new Criteria();

        criteria.and("userId").is(user.getId());

        if (filters.getCanteenName() != null && !filters.getCanteenName().isEmpty()) criteria.and("name").regex(filters.getCanteenName(), "i");
        if (filters.getCity() != null && !filters.getCity().isEmpty()) criteria.and("canteenId").is(filters.getCity());

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

    @Override
    public Canteen approveOrRejectCanteen(String canteenId, ApprovalRequest request) {
        Canteen canteen = canteenRepo.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with : " + canteenId));

        Approval approval = new Approval();
        approval.setStatus(request.getStatus());
        approval.setReasons(request.getReasons());
        approval.setDate(LocalDateTime.now());

        canteen.setApproval(approval);

        return canteenRepo.save(canteen);
    }



}

package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddCanteen;
import org.example.vegnbioapi.dto.Approval;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.Status;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.CanteenRepo;
import org.example.vegnbioapi.repository.UserRepo;
import org.example.vegnbioapi.service.CanteenService;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

        org.example.vegnbioapi.model.Approval app= new org.example.vegnbioapi.model.Approval();
        app.setStatus(Status.PENDING);
        canteen.setApproval(app);
        canteen.setApproval(app);

        canteen.setCreatedAt(LocalDateTime.now());
        List<String> picturesUrl = this.storageService.uploadPictures("canteen",pictures);
        canteen.setPictures(picturesUrl);
        return canteenRepo.save(canteen);
    }

    @Override
    public List<Canteen> getApprovedCanteens(CanteenFilter filters) { //Flutter
        Criteria criteria = new Criteria();

        log.info("filters : "+filters.toString());

        criteria.and("approval.status").is(Status.APPROVED);

        if (filters.getName() != null && !filters.getName().isEmpty()) {
            criteria.and("name").regex(filters.getName(), "i");
        }

        List<String> specificEquipments = new ArrayList<>();
        if (Boolean.TRUE.equals(filters.getHasAnimation())) {
            specificEquipments.add("Animation");
        }
        if (Boolean.TRUE.equals(filters.getHasConferenceRoom())) {
            specificEquipments.add("MeetingRoom");
        }
        if (Boolean.TRUE.equals(filters.getHasMeditation())) {
            specificEquipments.add("Espace de méditation");
        }
        if (!specificEquipments.isEmpty()) {
            criteria.and("equipments").in(specificEquipments);
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Canteen.class);
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

        if (filters.getName() != null && !filters.getName().isEmpty()) {
            criteria.and("name").regex(filters.getName(), "i");
        }

        if (filters.getCities() != null && !filters.getCities().isEmpty()) {
            criteria.and("location.city").in(filters.getCities());
        }

        if (filters.getServices() != null && !filters.getServices().isEmpty()) {
            criteria.and("equipments").in(filters.getServices());
        }

        if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
            criteria.and("approval.status").in(filters.getStatus());
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Canteen.class);
    }



    @Override
    public List<Canteen> loadFilteredCanteens(CanteenFilter filters) {

        Criteria criteria = new Criteria();

        if (filters.getName() != null && !filters.getName().isEmpty()) {
            criteria.and("name").regex(filters.getName(), "i");
        }

        if (filters.getCities() != null && !filters.getCities().isEmpty()) {
            criteria.and("location.city").in(filters.getCities());
        }

        if (filters.getServices() != null && !filters.getServices().isEmpty()) {
            criteria.and("equipments").in(filters.getServices());
        }

        if (filters.getStatus() != null && !filters.getStatus().isEmpty()) {
            criteria.and("approval.status").in(filters.getStatus());
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Canteen.class);
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
    public Canteen approveOrRejectCanteen(String canteenId, Approval request) {
        Canteen canteen = canteenRepo.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with : " + canteenId));

        org.example.vegnbioapi.model.Approval approval = new org.example.vegnbioapi.model.Approval();
        approval.setStatus(request.getStatus());
        approval.setReasons(request.getReasons());
        approval.setDate(LocalDateTime.now());
        canteen.setApproval(approval);
        return canteenRepo.save(canteen);
    }



}

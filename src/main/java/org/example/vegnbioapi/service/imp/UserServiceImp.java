package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.ERole;
import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.repository.UserRepo;
import org.example.vegnbioapi.service.StorageService;
import org.example.vegnbioapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StorageService storageService;

    @Override
    public Boolean approve(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepo.save(user);
        return true;
    }

    @Override
    public List<User> loadFilteredUsers(UserFilter filters) {

        log.info(filters.toString());

        Criteria criteria = new Criteria();
        Criteria rolesCriteria = Criteria.where("roles.role").not().in(Set.of(ERole.ADMIN));

        if (filters.getRoles() != null && !filters.getRoles().isEmpty()) {
            rolesCriteria = new Criteria().andOperator(
                    Criteria.where("roles.role").not().in(Set.of(ERole.ADMIN)),
                    Criteria.where("roles.role").in(filters.getRoles())
            );
        }
        criteria.andOperator(rolesCriteria);

        if (filters.getEmail() != null && !filters.getEmail().isEmpty()) {
            criteria.and("email").regex(filters.getEmail(), "i");
        }

        if(filters.getIsActive()!=null && !filters.getIsActive().isEmpty()){
            if(filters.getIsActive().equals("verified")){
                criteria.and("isActive").is(true);
            }else{
                criteria.and("isActive").is(false);
            }
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public List<String> saveRestorerDocs(String userId, List<MultipartFile> files) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with Id : " + userId));
        List<String> uploadedUrls = storageService.uploadPdf(user.getId(), files);

        if (user.getDocs() == null) {
            user.setDocs(new ArrayList<>());
        }
        user.getDocs().addAll(uploadedUrls);
        userRepo.save(user);
        return uploadedUrls;
    }

    public User verifyUser(String id) {
        User user = getUserById(id);
        user.setVerified(true);
        return userRepo.save(user);
    }

    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id:"+ id));
    }

    public User toggleActive(String id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        userRepo.save(user);
        return userRepo.save(user);
    }

    @Override
    public User delete(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        User userDeleted =  mongoTemplate.findAndRemove(query, User.class);
        assert userDeleted != null;
        log.info("deleted : "+ userDeleted.getId());
        return userDeleted;
    }
}

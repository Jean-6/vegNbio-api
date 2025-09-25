package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<User> loadFilteredUsers(UserFilter filters) {

        Query query = new Query();
        /*if(filters.getCanteenId() != null && !filters.getCanteenId().isEmpty()){
            query.addCriteria(Criteria.where("canteenId").is(eventFilter.getCanteenId()));
        }
        if(filters.getType() != null ){
            query.addCriteria(Criteria.where("type").gte(eventFilters.getType()));
        }
        if(eventFilters.getStartDate() != null ){
            query.addCriteria(Criteria.where("startDate").gte(eventFilters.getStartDate()));
        }
        if(eventFilters.getEndDate() != null ){
            query.addCriteria(Criteria.where("date").lte(eventFilters.getEndDate()));
        }*/
        return mongoTemplate.find(query, User.class);
    }
}

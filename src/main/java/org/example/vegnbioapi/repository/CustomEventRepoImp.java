package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;


@Repository
public  class CustomEventRepoImp implements  CustomEventRepo{

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public List<Event> findEventsWithFilters(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {

        Query query = new Query();
        if(restaurantId != null && !restaurantId.isEmpty()){
            query.addCriteria(Criteria.where("restaurantId").is(restaurantId));
        }
        if(startDate != null ){
            query.addCriteria(Criteria.where("date").gte(startDate));
        }
        if(endDate != null ){
            query.addCriteria(Criteria.where("date").lte(endDate));
        }
        return mongoTemplate.find(query, Event.class);
    }
}

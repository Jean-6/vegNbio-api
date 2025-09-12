package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.EventBooking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventBookingRepo extends MongoRepository<EventBooking,String> {
    List<EventBooking> findByUserId(String userId);
}

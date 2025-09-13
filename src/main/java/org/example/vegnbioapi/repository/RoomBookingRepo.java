package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.EventBooking;
import org.example.vegnbioapi.model.RoomBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoomBookingRepo extends MongoRepository<RoomBooking, String> {
    List<RoomBooking> findByUserId(String userId);
}

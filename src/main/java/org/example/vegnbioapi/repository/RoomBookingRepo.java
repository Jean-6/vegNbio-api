package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.RoomBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomBookingRepo extends MongoRepository<RoomBooking, String> {
}

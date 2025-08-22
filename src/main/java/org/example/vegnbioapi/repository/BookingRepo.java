package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends MongoRepository<Booking, String> {


}

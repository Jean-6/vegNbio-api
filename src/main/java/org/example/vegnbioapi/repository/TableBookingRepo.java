package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.TableBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TableBookingRepo extends MongoRepository<TableBooking, String> {
    List<TableBooking> findByUserId(String userId);
}

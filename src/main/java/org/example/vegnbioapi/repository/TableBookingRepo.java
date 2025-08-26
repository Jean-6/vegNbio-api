package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.TableBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TableBookingRepo extends MongoRepository<TableBooking, String> {
}

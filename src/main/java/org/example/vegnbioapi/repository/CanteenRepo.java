package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.Canteen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CanteenRepo extends MongoRepository<Canteen, String> {
}

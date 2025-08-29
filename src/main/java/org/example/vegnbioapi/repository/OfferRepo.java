package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepo extends MongoRepository<Offer,String> {
}

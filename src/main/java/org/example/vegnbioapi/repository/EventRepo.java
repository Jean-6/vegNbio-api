package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepo extends MongoRepository<Event, String> { }

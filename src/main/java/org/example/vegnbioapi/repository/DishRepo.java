package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DishRepo extends MongoRepository<Dish, String> {
}

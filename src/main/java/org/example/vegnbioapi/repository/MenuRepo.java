package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MenuRepo extends MongoRepository<Menu, String> {
    @Override
    Optional<Menu> findById(String s);
}

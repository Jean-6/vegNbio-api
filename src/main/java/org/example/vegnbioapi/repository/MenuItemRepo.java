package org.example.vegnbioapi.repository;

import org.example.vegnbioapi.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuItemRepo extends MongoRepository<MenuItem, String> {
}

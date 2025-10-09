package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {

    @Override
    Optional<User> findById(String s);
}

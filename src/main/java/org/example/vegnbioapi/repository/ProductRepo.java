package org.example.vegnbioapi.repository;


import org.example.vegnbioapi.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends MongoRepository<Product,String> {
}

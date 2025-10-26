package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddProductDto;
import org.example.vegnbioapi.dto.ProductFilter;
import org.example.vegnbioapi.model.Approval;
import org.example.vegnbioapi.model.Event;
import org.example.vegnbioapi.model.Product;
import org.example.vegnbioapi.model.Status;
import org.example.vegnbioapi.repository.ProductRepo;
import org.example.vegnbioapi.service.ProductService;
import org.example.vegnbioapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StorageService storageService;


    @Override
    public Product save(AddProductDto productDto, List<MultipartFile> pictures) throws IOException {

        Product product = new Product();
        product.setType(productDto.getType());
        product.setName(productDto.getName());
        product.setDesc(productDto.getDesc());
        product.setCategory(productDto.getCategory());
        product.setQuantity(productDto.getQuantity());
        product.setUnit(productDto.getUnit());
        product.setUnitPrice(productDto.getUnitPrice());
        product.setOrigin(productDto.getOrigin());
        product.setAvailabilityDate(productDto.getAvailabilityDate());
        //product.setExpirationDate(productDto.getExpirationDate());
        product.setSupplierId(productDto.getSupplierId());

        Approval app= new Approval();
        app.setStatus(Status.PENDING);
        product.setApproval(app);

        product.setCreatedAt(LocalDateTime.now());
        List<String> picturesUrl = this.storageService.uploadPictures("product",pictures);
        product.setPictures(picturesUrl);
        return productRepo.save(product);
    }


    @Override
    public List<Product> loadProducts(ProductFilter filters) {
        Query query = new Query();
        if(filters.getType() != null && !filters.getType().isEmpty()){
            query.addCriteria(Criteria.where("type").in(filters.getType()));
        }
        if(filters.getName() != null && !filters.getName().isEmpty() ){
            query.addCriteria(Criteria.where("name").in(filters.getName()));
        }
        if(filters.getCategory() != null && !filters.getCategory().isEmpty()  ){
            query.addCriteria(Criteria.where("category").in(filters.getCategory()));
        }

        if(filters.getMinPrice() != null && !filters.getMinPrice().isNaN()  ){
            query.addCriteria(Criteria.where("minPrice").gte(filters.getMinPrice()));
        }

        if(filters.getMaxPrice() != null && !filters.getMaxPrice().isNaN()  ){
            query.addCriteria(Criteria.where("maxPrice").lte(filters.getMaxPrice()));
        }

        if(filters.getOrigin() != null && !filters.getOrigin().isEmpty()  ){
            query.addCriteria(Criteria.where("origin").in(filters.getOrigin()));
        }

        return mongoTemplate.find(query, Product.class);

    }

    @Override
    public Product delete(String id) {
        Optional<Product> productOpt = productRepo.findById(id);
        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id : " + id);
        }
        Product product = productOpt.get();
        storageService.deleteFromS3(product.getPictures());
        productRepo.deleteById(product.getId());
        return  product;
    }

}

package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.OfferDto;
import org.example.vegnbioapi.dto.OfferFilter;
import org.example.vegnbioapi.model.Offer;
import org.example.vegnbioapi.repository.OfferRepo;
import org.example.vegnbioapi.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class OfferServiceImp implements OfferService {

    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private OfferRepo offerRepo;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Offer save(OfferDto offerDto, List<MultipartFile> pictures) {

        List<String> imgUrls = new ArrayList<>();

        for (int index = 0; index < pictures.size(); index++) {
            try {
                String filename = "img_" + index + "." + Utils.getExtension(pictures.get(index).getOriginalFilename());
                String key = "offer/" + Utils.generateFolderName() + "/" + filename;

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .contentType(pictures.get(index).getContentType())
                                .build(),
                        RequestBody.fromBytes(pictures.get(index).getBytes())
                );

                imgUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'upload du fichier : " + pictures.get(index).getOriginalFilename(), e);
            }
        }

        Offer offer = new Offer();
        offer.setType(offerDto.getType());
        offer.setName(offerDto.getName());
        offer.setDesc(offerDto.getDesc());
        offer.setCategory(offerDto.getCategory());
        offer.setQuantity(offerDto.getQuantity());
        offer.setUnit(offerDto.getUnit());
        offer.setUnitPrice(offerDto.getUnitPrice());
        offer.setOrigin(offerDto.getOrigin());
        offer.setPictures(imgUrls);
        offer.setAvailabilityDate(offerDto.getAvailabilityDate());
        offer.setExpirationDate(offerDto.getExpirationDate());
        offer.setSupplierId(offerDto.getSupplierId());
        return offerRepo.save(offer);
    }


    @Override
    public List<Offer> loadFilteredOffers( OfferFilter filters) {
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

        return mongoTemplate.find(query, Offer.class);

    }

}

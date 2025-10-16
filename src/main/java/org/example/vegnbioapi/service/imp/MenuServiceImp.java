package org.example.vegnbioapi.service.imp;


import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddMenuItem;
import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.*;
import org.example.vegnbioapi.repository.MenuItemRepo;
import org.example.vegnbioapi.repository.MenuRepo;
import org.example.vegnbioapi.service.MenuService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class MenuServiceImp implements MenuService {


    @Autowired
    private MenuItemRepo menuItemRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public MenuItem saveMenuItem(AddMenuItem dto,List<MultipartFile> pictures) throws IOException {

        MenuItem menuItemToSave = null ;

        log.info("drink:"+ dto.toString());

        switch (dto.getItemType().toLowerCase()) {
            case "drink" -> {
                Drink drink = new Drink();
                drink.setCanteenId(dto.getCanteenId());
                drink.setName(dto.getName());
                drink.setDesc(dto.getDesc());
                drink.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
                drink.setVolume(dto.getVolume() );
                drink.setAlcoholic(dto.getIsAlcoholic());
                drink.setGaseous(dto.getIsGaseous());
                menuItemToSave = drink;
            }
            case "meal" -> {
                Meal meal = new Meal();
                meal.setCanteenId(dto.getCanteenId());
                meal.setName(dto.getName());
                meal.setDesc(dto.getDesc());
                meal.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
                meal.setIngredients(dto.getIngredients());
                meal.setAllergens(dto.getAllergens());
                meal.setFoodType(dto.getFoodType());
                menuItemToSave = meal;
            }
        }

        assert menuItemToSave != null;
        List<String> pictureUrls = savePictures(pictures);
        menuItemToSave.setImageUrls(pictureUrls);
        return menuItemRepo.save(menuItemToSave);
    }


    @Override
    public List<String> savePictures(List<MultipartFile> pictures) throws IOException {
        List<String> imgUrls = new ArrayList<>();

        for(int index = 0; index< pictures.size(); index++){
            String filename = "img_"+ index + "."+Utils.getExtension(pictures.get(index).getOriginalFilename());
            String key = "menuItem/" + Utils.generateFolderName() + "/"+filename ;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(pictures.get(index).getContentType())
                            .build(),
                    RequestBody.fromBytes(pictures.get(index).getBytes()));
            imgUrls.add("https://" + bucketName + ".s3.amazonaws.com/" + key);
        }
        return imgUrls;
    }

    @Override
    public Menu saveMenu(MenuDto menuDto) {

        Menu menu = new Menu();
        menu.setId(menuDto.getId());
        menu.setCanteenId(menuDto.getCanteenId());
        menu.setName(menuDto.getName());
        menu.setDesc(menuDto.getDesc());
        return menuRepo.save(menu);
    }

    @Override
    public Optional<Menu> loadById(String id) {
        return Optional.of(menuRepo.findById(id)
                .orElseThrow());
    }

    @Override
    public List<Menu> loadFilteredMenus(String restaurantId, String name, String diet) {
        Query query = new Query();
        if(restaurantId != null && !restaurantId.isEmpty()){
            query.addCriteria(Criteria.where("restaurantId").is(restaurantId));
        }
        if(name != null ){
            query.addCriteria(Criteria.where("name").in(name));
        }
        if(diet != null ){
            query.addCriteria(Criteria.where("dietType").in(diet));
        }
        query.addCriteria(Criteria.where("dishes").exists(true).ne(Collections.emptyList()));
        return mongoTemplate.find(query, Menu.class);

    }

    @Override
    public void deleteMenu(String id) {
        if (!menuRepo.existsById(id)) {
            throw new RuntimeException("Menu not found with id: " + id);
        }
        menuRepo.deleteById(id);

    }

    @Override
    public MenuItem deleteMenuItem(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        MenuItem menuItemDeleted = mongoTemplate.findAndRemove(query, MenuItem.class);

        if (menuItemDeleted == null) {
            throw new RuntimeException("Menu not found with id: " + id);
        }

        log.info("Deleted menu item with id: {}", menuItemDeleted.getId());
        return menuItemDeleted;
    }
}

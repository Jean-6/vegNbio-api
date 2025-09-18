package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.Menu;
import org.example.vegnbioapi.repository.MenuRepo;
import org.example.vegnbioapi.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class MenuServiceImp implements MenuService {

    @Autowired
    private MenuRepo menuRepo;
    @Value("${aws.s3-bucket-name}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private MongoTemplate mongoTemplate;



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

}

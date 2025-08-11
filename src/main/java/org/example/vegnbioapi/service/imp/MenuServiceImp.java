package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.MenuDto;
import org.example.vegnbioapi.model.Menu;
import org.example.vegnbioapi.repository.MenuRepo;
import org.example.vegnbioapi.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
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


    @Override
    public Menu saveMenu(MenuDto menuDto) {

        Menu menu = new Menu();
        menu.setId(menuDto.getId());
        menu.setRestaurantId(menuDto.getRestaurantId());
        menu.setName(menuDto.getName());
        menu.setDesc(menuDto.getDesc());
        return menuRepo.save(menu);
    }

    /*@Override
    public List<Menu> getMenus(String restaurantId, LocalDateTime startDate, LocalDateTime endDate) {
        return menuRepo.findAll();
    }*/

    @Override
    public Optional<Menu> loadById(String id) {
        return Optional.of(menuRepo.findById(id)
                .orElseThrow());
    }

    @Override
    public List<Menu> loadAll() {
        return menuRepo.findAll();
    }


}

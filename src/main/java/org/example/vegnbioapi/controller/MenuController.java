package org.example.vegnbioapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddMenuItem;
import org.example.vegnbioapi.dto.DishDto;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.model.Dish;
import org.example.vegnbioapi.model.Menu;
import org.example.vegnbioapi.model.MenuItem;
import org.example.vegnbioapi.repository.MenuRepo;
import org.example.vegnbioapi.service.MenuService;
import org.example.vegnbioapi.service.S3Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private S3Storage s3Storage;
    @Autowired
    private ObjectMapper objectMapper;

    public Dish convertDtoToModel(DishDto dto) {
        Dish dish = objectMapper.convertValue(dto, Dish.class);
        dish.setId(UUID.randomUUID().toString());
        return dish;
    }



    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMenuItem(
            @RequestPart("data") String menuItemJson,
            @RequestPart(value = "pictures", required = false) List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        AddMenuItem dto = objectMapper.readValue(menuItemJson, AddMenuItem.class);

        MenuItem saved = menuService.saveMenuItem(dto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("menu saved", request.getRequestURI(), saved));
    }

    /*@PostMapping(value="/", consumes = MediaType.APPLICATION_JSON_VALUE, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Menu>> save(
            @RequestBody MenuDto menuDto,
            HttpServletRequest request) throws IOException {

            log.info(">> Save a menu ");
            log.debug(">> menuDto  : {}", menuDto.toString());
            Menu newMenu = menuService.saveMenu(menuDto);
            return ResponseEntity.ok(
                    ResponseWrapper.ok("menu saved", request.getRequestURI(), newMenu));
    }*/

    @PatchMapping(value="/{id}/dishes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Menu>> updateMenu(@PathVariable String id,
                                                            @RequestPart("dish") DishDto dishDto,
                                                            @RequestPart("pictures") List<MultipartFile> pictures,
                                                            HttpServletRequest hsr){
        return  menuRepo.findById(id)
                .map(menu -> {
                    List<String> imageUrls = s3Storage.upload(pictures);
                    Dish dish = convertDtoToModel(dishDto);

                    dish.setId(UUID.randomUUID().toString());

                    dish.setPictures(imageUrls);

                    if(menu.getDishes() == null){
                        menu.setDishes(new ArrayList<>());
                    }
                    menu.getDishes().add(dish);
                    return ResponseEntity.ok(
                            ResponseWrapper.ok("menu updated",hsr.getRequestURI(),menuRepo.save(menu)));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<Menu>>> get(

            @RequestParam(required = false) String restaurantId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dietType,
            HttpServletRequest hsr) {
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "all menus",hsr.getRequestURI(),menuService.loadFilteredMenus(restaurantId,name,dietType)));
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteMenu(@PathVariable String id,HttpServletRequest hsr) {
        log.info(">> delete menu ");
        menuService.deleteMenu(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "menu deleted : {}",id,hsr.getRequestURI()
                )
        );
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteItemMenu(@PathVariable String id,HttpServletRequest hsr) {
        log.info(">> delete menu ");

        MenuItem itemMenu= menuService.deleteMenuItem(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "menu deleted : {}",id,hsr.getRequestURI()
                )
        );
    }

}

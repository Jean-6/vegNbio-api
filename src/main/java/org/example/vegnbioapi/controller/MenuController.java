package org.example.vegnbioapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Dish;
import org.example.vegnbioapi.model.MenuItem;
import org.example.vegnbioapi.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/menu")
@Tag(name = "Menu Controller", description = "Manages item menu ")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private ObjectMapper objectMapper;

    public Dish convertDtoToModel(DishDto dto) {
        Dish dish = objectMapper.convertValue(dto, Dish.class);
        dish.setId(UUID.randomUUID().toString());
        return dish;
    }


    @GetMapping(value="/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<MenuItem>>> get(
            @ModelAttribute ItemMenuFilter filters,
            Principal principal,
            HttpServletRequest hsr) {

        log.info(">> Load all items menu for current user");
        List<MenuItem> items = menuService.getItemMenuForCurrentUser(principal,filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Items menu loaded", hsr.getRequestURI(), items));
    }

    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<MenuItem>>> get(
            @ModelAttribute ItemMenuFilter filters,
            HttpServletRequest hsr) {
        List<MenuItem> items = menuService.getItemsMenu(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "Items menu loaded",hsr.getRequestURI(),items));
    }



    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMenuItem(
            Principal principal,
            @RequestPart("data") String menuItemJson,
            @RequestPart(value = "pictures", required = false) List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        AddMenuItem dto = objectMapper.readValue(menuItemJson, AddMenuItem.class);

        MenuItem saved = menuService.saveMenuItem(principal,dto, pictures);
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

    /*@PatchMapping(value="/{id}/dishes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Menu>> updateMenu(@PathVariable String id,
                                                            @RequestPart("dish") DishDto dishDto,
                                                            @RequestPart("pictures") List<MultipartFile> pictures,
                                                            HttpServletRequest hsr){
        return  menuRepo.findById(id)
                .map(menu -> {
                    List<String> imageUrls = storageService.uploadPictures("menu",pictures);
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
    }*/


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

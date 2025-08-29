package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.CanteenDto;
import org.example.vegnbioapi.dto.CanteenFilter;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/canteen")
public class CanteenController {


    @Autowired
    private CanteenService canteenService;


    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Canteen>> save(
            @RequestPart("data") CanteenDto canteenDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {
        log.info(">> Save a canteen ");
        log.debug(">> canteenDto  : {}", canteenDto.toString());
        Canteen  canteen = canteenService.saveCanteen(canteenDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("canteen saved", request.getRequestURI(), canteen));
    }


    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<Canteen>>> get(
            @ModelAttribute CanteenFilter filters,
            HttpServletRequest request) {

        log.info(">> Load all canteens");

        List<Canteen> canteens = canteenService.loadFilteredCanteens(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Canteen list", request.getRequestURI(), canteens));
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Canteen>> delete(
            @PathVariable("id") String id,
            HttpServletRequest request)  {

        log.info("id : "+ id);

        log.info(">> Delete a canteen ");
        Canteen  canteen =  canteenService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Canteen deleted", request.getRequestURI(), canteen));
    }
}

package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.Canteen;
import org.example.vegnbioapi.service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/canteen")
public class CanteenController {


    @Autowired
    private CanteenService canteenService;


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Canteen>> get(
            @PathVariable String id,
            HttpServletRequest request) {

        log.info(">> Load canteen by id");

        Canteen canteen = canteenService.loadById(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Get canteen by ID ", request.getRequestURI(), canteen));
    }
    
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<Canteen>>> get(
            @ModelAttribute CanteenFilter filters,
            Principal principal,
            HttpServletRequest hsr) {

        log.info(">> Load all canteens for current user");
        log.info("filters : "+ filters.toString());
        List<Canteen> canteens = canteenService.getCanteenForCurrentUser(principal, filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Canteen list", hsr.getRequestURI(), canteens));
    }

    @GetMapping("me/approved")
    public ResponseEntity<ResponseWrapper<List<Canteen>>> getApprovedCanteensForCurrentUser(Principal principal,
                                                                           HttpServletRequest hsr) {

        log.info(">> Load all canteens approved for current user");
        List<Canteen> approvedCanteens = canteenService.getApprovedCanteensForCurrentUser(principal);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Canteen list", hsr.getRequestURI(), approvedCanteens));
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

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Canteen>> save(
            @RequestPart("data") AddCanteen canteenDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {
        log.info(">> Save a canteen ");
        log.debug(">> canteenDto  : {}", canteenDto.toString());
        Canteen  canteen = canteenService.saveCanteen(canteenDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("canteen saved", request.getRequestURI(), canteen));
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


    @PutMapping("/approve/{id}")
    public ResponseEntity<Canteen> approveOrRejectCanteen(
            @PathVariable String id,
            @RequestBody ApprovalRequest request
    ) {
        Canteen updated = canteenService.approveOrRejectCanteen(id, request);
        return ResponseEntity.ok(updated);
    }
}

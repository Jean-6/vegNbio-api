package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.OfferDto;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.OfferFilter;
import org.example.vegnbioapi.model.Offer;
import org.example.vegnbioapi.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/offer")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Offer>> save(
            @RequestPart("data") OfferDto offerDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        log.info(">> Save an offer ");
        log.debug(">> offerDto  : {}", offerDto.toString());

        Offer offer = offerService.save(offerDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("offer saved", request.getRequestURI(), offer));
    }

    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List <Offer>>> getAllOffers(
            @ModelAttribute OfferFilter filters,
            HttpServletRequest request) {

        log.info(">> Load filtered offers  ");
        log.debug(">> offerDto  : {}", filters);
        log.info(">> Get all offers ");
        List<Offer> offers = offerService.loadFilteredOffers(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("offer saved", request.getRequestURI(), offers));
    }


}

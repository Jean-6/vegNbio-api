package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddProductDto;
import org.example.vegnbioapi.dto.ProductDto;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.ProductFilter;
import org.example.vegnbioapi.model.Product;
import org.example.vegnbioapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Product>> save(
            @RequestPart("data") AddProductDto productDto,
            @RequestPart("pictures") List<MultipartFile> pictures,
            HttpServletRequest request) throws IOException {

        log.info(">> Save an offer ");
        log.debug(">> offerDto  : {}", productDto.toString());

        Product product = productService.save(productDto, pictures);
        return ResponseEntity.ok(
                ResponseWrapper.ok("offer saved", request.getRequestURI(), product));
    }

    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List <Product>>> getAllProducts(
            @ModelAttribute ProductFilter filters,
            HttpServletRequest request) {

        log.info(">> Load filtered products  ");
        log.debug(">> productDto  : {}", filters);
        log.info(">> Get all products ");
        List<Product> products = productService.loadProducts(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("load product", request.getRequestURI(), products));
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<Product>> delete(
            @PathVariable("id") String id,
            HttpServletRequest request)  {
        log.info("id : "+ id);
        log.info(">> Delete a offer ");
        Product product =  productService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Offer deleted with ID: "+id , request.getRequestURI(), product));
    }


}

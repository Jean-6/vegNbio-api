package org.example.vegnbioapi.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.AddProductDto;
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
@Tag(name = "Product Controller", description = "Manages supplier products to buy")
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
            @RequestParam(required = false) String supplierId,
            @ModelAttribute ProductFilter filters,
            HttpServletRequest request) {

        log.info(">> Load filtered products  ");
        log.debug(">> productDto  : {}", filters);
        log.info(">> Get all products ");
        List<Product> products ;
        if (supplierId != null) {
            log.info(">> Get products for supplier id: {}", supplierId);
            products = productService.loadProductsBySupplierId(supplierId, filters);
        } else {
            log.info(">> Get all products");
            products = productService.loadProducts(filters);
        }
        return ResponseEntity.ok(
                ResponseWrapper.ok("load product", request.getRequestURI(), products));
    }

    @GetMapping(value = "/approved", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<Product>>> getApprovedProducts(
            @ModelAttribute ProductFilter filters,
            HttpServletRequest request) {

        log.info(">> Load approved products");
        log.debug(">> ProductFilter: {}", filters);

        List<Product> approvedProducts = productService.loadApprovedProducts(filters);

        return ResponseEntity.ok(
                ResponseWrapper.ok("Approved products loaded", request.getRequestURI(), approvedProducts)
        );
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



    @PutMapping("/{id}/reject")
    public ResponseEntity<ResponseWrapper<Product>> rejectProduct(
            @PathVariable String id,
            @RequestParam(required = false) String reasons,
            HttpServletRequest request) {

        Product rejectedProduct = productService.rejectProduct(id, reasons);

        return ResponseEntity.ok(
                ResponseWrapper.ok("Product rejected", request.getRequestURI(), rejectedProduct)
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ResponseWrapper<Product>> approveProduct(
            @PathVariable String id,
            @RequestParam(required = false) String reasons,
            HttpServletRequest request) {

        Product approvedProduct = productService.approveProduct(id, reasons);

        return ResponseEntity.ok(
                ResponseWrapper.ok("Product approved", request.getRequestURI(), approvedProduct)
        );
    }

}

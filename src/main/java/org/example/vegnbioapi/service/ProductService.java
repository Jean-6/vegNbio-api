package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.AddProductDto;
import org.example.vegnbioapi.dto.ProductFilter;
import org.example.vegnbioapi.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {


    List<Product> loadProductsBySupplierId(String supplierId, ProductFilter filters);
    Product save(AddProductDto productDto, List<MultipartFile> images) throws IOException;
    List<Product> loadApprovedProducts(ProductFilter filters);
    List<Product> loadProducts(ProductFilter filters);
    Product delete(String id);
    Product rejectProduct(String id, String reasons);
    Product approveProduct(String id, String reasons);

}

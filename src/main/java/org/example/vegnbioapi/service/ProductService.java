package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.AddProductDto;
import org.example.vegnbioapi.dto.ProductDto;
import org.example.vegnbioapi.dto.ProductFilter;
import org.example.vegnbioapi.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    
    Product save(AddProductDto productDto, List<MultipartFile> images) throws IOException;
    List<Product> loadProducts(ProductFilter filters);
    Product delete(String id);

}

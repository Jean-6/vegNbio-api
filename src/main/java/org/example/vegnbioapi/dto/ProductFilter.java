package org.example.vegnbioapi.dto;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProductFilter {
    private String[] type;
    private String name;
    private String[] category;
    private Double minPrice;
    private Double maxPrice;
    private String origin;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}

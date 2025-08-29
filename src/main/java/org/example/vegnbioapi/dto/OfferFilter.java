package org.example.vegnbioapi.dto;


import lombok.Data;

@Data
public class OfferFilter {
    private String type;
    private String name;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private String origin;
}

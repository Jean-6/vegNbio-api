package org.example.vegnbioapi.dto;


import lombok.Data;

import java.math.BigDecimal;


@Data
public class ItemMenuFilter {

    private String itemType;
    private String canteenId;
    private String canteenName;
    private String itemName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}

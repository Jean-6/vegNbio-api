package org.example.vegnbioapi.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddMenuItem {

    private String itemType;
    private String canteenId;
    private String itemName;
    private String desc;
    private BigDecimal price;

    // Drink
    private String volume;
    private Boolean isGaseous = false;
    private Boolean isAlcoholic = false;

    // Meal
    private List<String> ingredients;
    private List<String> allergens;
    private String foodType;
}

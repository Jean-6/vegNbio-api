package org.example.vegnbioapi.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddMenuItem {

    private String itemType; // "drink" ou "meal"
    private String canteenId;
    private String name;
    private String desc;
    private BigDecimal price;
    private String userId;

    // Champs spécifiques à Drink
    private String volume;
    private Boolean isGaseous;
    private Boolean isAlcoholic;

    // Champs spécifiques à Meal
    private List<String> ingredients;
    private List<String> allergens;
    private String foodType;
}

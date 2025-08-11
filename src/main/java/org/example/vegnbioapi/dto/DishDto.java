package org.example.vegnbioapi.dto;

import lombok.Data;
import org.example.vegnbioapi.model.DietType;

import java.util.List;
import java.util.Set;


@Data
public class DishDto {

    private String category;
    private String name;
    private String desc;
    private double price;
    private List<String> allergens;
    private Set<DietType> dietTypeSet;
}

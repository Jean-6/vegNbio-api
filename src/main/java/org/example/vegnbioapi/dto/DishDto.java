package org.example.vegnbioapi.dto;

import lombok.Data;
import org.example.vegnbioapi.model.Diet;
import org.example.vegnbioapi.model.Type;

import java.util.List;
import java.util.Set;


@Data
public class DishDto {

    private String category;
    private String name;
    private String desc;
    private double price;
    private Type type;
    private List<String> allergens;
    private Set<Diet> diet;
}

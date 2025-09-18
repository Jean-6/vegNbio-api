package org.example.vegnbioapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;


@Data
@Document
public class Dish {
    @Id
    private String id;
    private String name;
    private String desc;
    private double price;
    private Type type;
    private List<String> allergens; //[Gluten,Crustacés,Œufs,Poisson]
    private List<String> pictures;
    private Set<Diet> diet;
}

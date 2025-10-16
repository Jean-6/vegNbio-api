package org.example.vegnbioapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Meal extends MenuItem {

    private List<String> ingredients;
    private List<String> allergens;
    private String foodType;
}

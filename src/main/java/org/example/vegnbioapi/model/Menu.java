package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document
public class Menu {
    @Id
    private String id;
    private String restaurantId;
    private String name;
    private String desc;
    private List<Dish> dishes;
}

package org.example.vegnbioapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@Document
public abstract class MenuItem {

    @Id
    private String id;
    private String itemType;
    private String canteenId;
    private String itemName;
    private String desc;
    private BigDecimal price;
    private List<String> pictures = new ArrayList<>();
    private String userId;
}


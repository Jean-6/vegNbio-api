package org.example.vegnbioapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class Product {
    @Id
    private String id;
    private String type;
    private String name;
    private String desc;
    private String category;
    private double quantity;
    private String unit;
    private double unitPrice;
    private String origin;
    List<String> pictures;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    private String supplierId;
    private Approval approval;
    private LocalDateTime createdAt;
}

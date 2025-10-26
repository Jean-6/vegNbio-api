package org.example.vegnbioapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.vegnbioapi.model.Approval;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class ProductDto {
    private String type;
    private String name;
    private String desc;
    private String category;
    private double quantity;
    private String unit;
    private double unitPrice;
    private String origin;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    private UserInfo user;
    private Approval approval;
    private LocalDateTime createdAt;
}

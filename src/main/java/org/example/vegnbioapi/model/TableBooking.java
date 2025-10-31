package org.example.vegnbioapi.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.vegnbioapi.dto.CanteenInfo;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Document
public class TableBooking {

    private String id;
    //private String canteenId;
    //private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Integer  people = 0;
    private String userId;
    private CanteenInfo canteenInfo;
    private LocalDateTime createdAt;
}

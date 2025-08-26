package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Document
public class TableBooking {

    private String id;
    private String canteenId;
    private String name;
    private LocalTime startTime;
    private LocalDate date;
    //private Status status;
    private Integer people;
    private String userId;
    private LocalDateTime createdAt;
}

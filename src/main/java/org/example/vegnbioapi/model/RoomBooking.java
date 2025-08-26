package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Document
public class RoomBooking {

    private String id;
    private String canteenId;
    private String name;
    private Integer roomNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private Status status;
    private Integer people;
    private String userId;
    //private Status status;
    private LocalDateTime createdAt;



}

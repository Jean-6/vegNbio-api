package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Document
public class Booking {

    private String id;
    private String restaurantId;
    private String resourceId; // table ou room Id
    private BookingType type;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private Status status;
    private LocalDateTime createdAt;
    private Integer numberOfSeats;
    private String userId;



}

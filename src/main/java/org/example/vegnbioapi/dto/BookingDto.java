package org.example.vegnbioapi.dto;

import org.example.vegnbioapi.model.Status;

import java.time.LocalDateTime;
import java.util.Date;

public class BookingDto {

    private String restaurantId;
    private String userId;
    private String type; //Room or Table
    private Date date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int people;
    private Status status;
    private LocalDateTime createdAt;
}

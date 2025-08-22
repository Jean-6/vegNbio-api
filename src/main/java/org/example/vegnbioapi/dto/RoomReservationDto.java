package org.example.vegnbioapi.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RoomReservationDto {
    private String roomId;
    private String restaurantId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String userId;
}

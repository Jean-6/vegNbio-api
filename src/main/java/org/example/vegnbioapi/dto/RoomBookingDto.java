package org.example.vegnbioapi.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RoomBookingDto {
    private String canteenId;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private Integer people;
    private String userId;
}

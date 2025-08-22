package org.example.vegnbioapi.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TableReservationDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer numberOfSeats;
    private String userId;
}

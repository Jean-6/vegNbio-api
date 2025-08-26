package org.example.vegnbioapi.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TableBookingDto {
    private String canteenId;
    private String name;
    private LocalTime startTime;
    private LocalDate date;
    private Integer people;
    private String userId;
}

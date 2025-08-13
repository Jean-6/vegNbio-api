package org.example.vegnbioapi.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
public class CanteenSearchDto {

    private String restaurantName;
    private String dishName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private Integer capacity;
    private Boolean hasWifi;
    private Boolean hasPrinter;
    private Boolean hasConferenceRoom;

    // Getters and setters (ou utiliser Lombok avec @Data)
}

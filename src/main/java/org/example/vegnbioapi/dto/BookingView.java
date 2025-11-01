package org.example.vegnbioapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingView {
    private String id;
    private String type; // "TABLE", "ROOM", "EVENT"

    private String canteenId;
    private String canteenName;

    private UserInfo userInfo;
    private CanteenInfo canteenInfo;



    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    private Integer people;
    private LocalDateTime createdAt;
}

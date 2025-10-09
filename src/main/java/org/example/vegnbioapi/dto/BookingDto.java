package org.example.vegnbioapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
public class BookingDto {

    private String type; //Room , Table or Event
    private int people;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate date;
    private UserInfo userInfo;
    private CanteenInfo canteenInfo;
    private LocalDateTime createdAt;

    // Event
    public BookingDto(String type,  LocalTime startTime, LocalTime endTime, LocalDate date,
            UserInfo userInfo,
            CanteenInfo canteenInfo,
            LocalDateTime createdAt){
                this.type= type;
                this.startTime = startTime;
                this.endTime = endTime;
                this.date = date;
                this.userInfo = userInfo;
                this.canteenInfo = canteenInfo;
                this.createdAt  = createdAt;
    }

    // Table
    public BookingDto(String type,  LocalTime startTime,  LocalDate date, int people, UserInfo userInfo,
                      CanteenInfo canteenInfo,
                      LocalDateTime createdAt){
        this.type= type;
        this.startTime = startTime;
        this.date = date;
        this.people = people;
        this.userInfo = userInfo;
        this.canteenInfo = canteenInfo;
        this.createdAt  = createdAt;
    }
}

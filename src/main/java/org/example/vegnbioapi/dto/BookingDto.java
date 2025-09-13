package org.example.vegnbioapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.vegnbioapi.model.Location;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
public class BookingDto {

    private String title;
    private String type; //Room or Table
    private String canteenName;
    private Location location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate date;

    /*
    private int people;
    private Status status;
    private String userId;
    private LocalDateTime createdAt;*/

    public BookingDto(  String type, String canteenName, Location location, LocalTime startTime,LocalDate date){
        this.type = type;
        this.canteenName = canteenName;
        this.location = location;
        this.startTime= startTime;
        this.date = date;

    }
}

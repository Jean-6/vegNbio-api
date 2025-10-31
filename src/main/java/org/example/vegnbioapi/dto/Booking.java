package org.example.vegnbioapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String type; // "ROOM", "TABLE" ou "EVENT"

    private int people;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private UserInfo userInfo;

    private CanteenInfo canteenInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Méthode utilitaire pour créer un Booking Table
    public static Booking tableBooking(String type, LocalTime startTime, LocalDate date, int people,
                                       UserInfo userInfo, CanteenInfo canteenInfo) {
        return Booking.builder()
                .type(type)
                .startTime(startTime)
                .date(date)
                .people(people)
                .userInfo(userInfo)
                .canteenInfo(canteenInfo)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Méthode utilitaire pour créer un Booking Room ou Event
    public static Booking roomOrEventBooking(String type, LocalTime startTime, LocalTime endTime,
                                             LocalDate date, UserInfo userInfo, CanteenInfo canteenInfo) {
        return Booking.builder()
                .type(type)
                .startTime(startTime)
                .endTime(endTime)
                .date(date)
                .userInfo(userInfo)
                .canteenInfo(canteenInfo)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

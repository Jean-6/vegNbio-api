package org.example.vegnbioapi.dto;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import software.amazon.awssdk.annotations.NotNull;

import java.time.LocalDate;

@Data
public class BookingFilter {
    private String canteenId;
    private String name;
    private String type;
    @NotNull
    private String userId;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}

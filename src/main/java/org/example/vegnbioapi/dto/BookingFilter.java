package org.example.vegnbioapi.dto;


import lombok.Data;
import software.amazon.awssdk.annotations.NotNull;

import java.time.LocalDate;

@Data
public class BookingFilter {
    private String type; // Table - Event - Room
    @NotNull
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
}

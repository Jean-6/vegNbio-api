package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import software.amazon.awssdk.annotations.NotNull;

import java.time.LocalDate;

@Data
public class BookingFilter {
    private String canteenId;
    private String canteenName;
    private String type;
    @NotNull
    private String userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}

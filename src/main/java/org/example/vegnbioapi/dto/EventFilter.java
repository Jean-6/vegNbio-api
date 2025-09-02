package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilter {
    private String canteenId;
    private String type;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime endDate;
}

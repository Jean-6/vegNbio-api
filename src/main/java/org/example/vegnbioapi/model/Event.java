package org.example.vegnbioapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;



@Document
@Data
public class Event {
    @Id
    private String id;
    private String canteenId;
    private String title;
    private String desc;
    private String type;
    private Location location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate date;
    private EventStatus status;
    private List<String> pictures;
    private List<String> participantsIds;
    private Approval approval;
    private LocalDateTime createdAt;
}


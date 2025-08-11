package org.example.vegnbioapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;



@Document
@Data
public class Event {
    @Id
    private String id;
    private String restaurantId;
    private String title;
    private String desc;
    private String category; //degustation, atelier, conférence…
    private Location location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> pictures;
    private List<String> participantsIds;
}


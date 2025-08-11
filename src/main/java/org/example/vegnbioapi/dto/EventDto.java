package org.example.vegnbioapi.dto;


import lombok.Data;
import org.example.vegnbioapi.model.Location;


import java.time.LocalDateTime;
import java.util.List;


@Data
public class EventDto {
    private String restaurantId;
    private String title;
    private String desc;
    private String category;
    private Location location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> participantsIds;
}

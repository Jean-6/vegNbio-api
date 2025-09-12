package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.vegnbioapi.model.Location;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Data
public class EventDto {
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
    private List<String> participantsIds;
}

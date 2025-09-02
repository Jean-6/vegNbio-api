package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.vegnbioapi.model.Location;


import java.time.LocalDate;
import java.util.List;


@Data
public class EventDto {
    private String canteenId;
    private String title;
    private String desc;
    private String type;
    private Location location;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    private List<String> participantsIds;
}

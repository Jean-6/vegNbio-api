package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
public class EventDto {

    @Id
    private String id;
    private CanteenInfo canteenInfo;
    private String title;
    private String desc;
    private String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd-MM-yyy")
    private LocalDate date;
    private List<String> pictures;
    private LocalDateTime createdAt;
}

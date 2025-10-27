package org.example.vegnbioapi.dto;

import lombok.Data;
import java.util.List;


@Data
public class CanteenFilter {

    private String name;
    private List<String> cities;
    private List<String> services;

    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //private LocalDateTime startDate;
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //private LocalDateTime endDate;
    private Boolean hasConferenceRoom;
    private Boolean hasMeal;
    private Boolean hasAnimation;
    private Boolean hasMeditation;
    private List<String> status;
}

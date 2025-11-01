package org.example.vegnbioapi.dto;

import lombok.Data;
import java.util.List;


@Data
public class CanteenFilter {

    private String name;
    private List<String> cities;
    private List<String> services;

    private Boolean hasConferenceRoom;
    private Boolean hasAnimation;
    private Boolean hasMeditation;
    private List<String> status;
}

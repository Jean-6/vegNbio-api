package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
@Document
public class Canteen {

    @Id
    private String id;
    private String name;
    private String desc;
    private List<String> equipments;
    private int seats;
    private int meetingRooms;
    private Map<DayOfWeek, OpeningHours> openingHoursMap;
    private Location location;
    private Contact contact;
    private List<String> tags; // ex: ["vegan", "bio", "local"]
    private List<String> menuIds; // références vers les menus
    private List<String> imageUrl;
}

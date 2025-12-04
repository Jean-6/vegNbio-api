package org.example.vegnbioapi.dto;

import lombok.Data;
import org.example.vegnbioapi.model.Contact;
import org.example.vegnbioapi.model.Location;
import org.example.vegnbioapi.model.OpeningHours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class AddCanteen {

    private String name;
    private String desc;
    private Contact contact;
    private List<String> equipments;
    private int meetingRooms;
    private Map<String, OpeningHours> openingHoursMap = new HashMap<>();
    private Location location;
    private List<String> tags; // ex: ["vegan", "bio", "local"]
    private String userId;
}

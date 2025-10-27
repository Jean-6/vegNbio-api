package org.example.vegnbioapi.dto;
import lombok.Data;
import org.example.vegnbioapi.model.Contact;
import org.example.vegnbioapi.model.Location;
import org.example.vegnbioapi.model.OpeningHours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CanteenDto {
    private String name;
    private String desc;
    private List<String> equipments;
    private int seats;
    private int meetingRooms;
    private Map<String, OpeningHours> openingHoursMap = new HashMap<>();
    private Location location;
    private Contact contact;
    private List<String> tags; // ex: ["vegan", "bio", "local"]
    private List<String> menuIds; // références vers les menus
    private List<String> pictures;
    private String userId;
}

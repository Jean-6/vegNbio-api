package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document
public class Canteen {
    @Id
    private String id;
    private String name;
    private String desc;
    private List<String> equipments; //Animation,conférence,Meditation"

    private int meetingRooms;
    private Map<String, OpeningHours> openingHoursMap = new HashMap<>();
    private Location location;
    private Contact contact;
    private List<String> tags; // ex: ["vegan", "bio", "local"]
    private List<String> menuIds; // références vers les menus
    private List<String> pictures;
    private String userId;
    private Approval approval;
    private LocalDateTime createdAt;

}

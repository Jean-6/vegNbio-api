package org.example.vegnbioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.vegnbioapi.model.Contact;
import org.example.vegnbioapi.model.Location;

@Data
@AllArgsConstructor
public class CanteenInfo {
    private String name;
    private Location location;
    private Contact contact;
}

package org.example.vegnbioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vegnbioapi.model.Contact;
import org.example.vegnbioapi.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanteenInfo {
    private String canteenId;
    private String name;
    private Location location;
    private Contact contact;
}

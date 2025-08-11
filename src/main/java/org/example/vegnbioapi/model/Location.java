package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Location {
    private String address;
    private String city;
    private String postalCode;
    private String country;
}

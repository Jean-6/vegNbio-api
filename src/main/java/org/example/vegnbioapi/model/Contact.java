package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Contact {
    private String phone;
    private String email;
}

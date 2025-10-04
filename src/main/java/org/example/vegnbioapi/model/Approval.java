package org.example.vegnbioapi.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Approval {
    private Status status;
    private String reasons;
    private LocalDateTime date;
}

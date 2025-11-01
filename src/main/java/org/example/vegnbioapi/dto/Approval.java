package org.example.vegnbioapi.dto;

import lombok.Data;
import org.example.vegnbioapi.model.Status;

import java.time.LocalDateTime;


@Data
public class Approval {
    private Status status;
    private String reasons;
    private LocalDateTime createdAt;
}
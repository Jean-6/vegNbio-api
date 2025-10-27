package org.example.vegnbioapi.dto;

import lombok.Data;
import org.example.vegnbioapi.model.Status;


@Data
public class Approval {
    private Status status;
    private String reasons;
}
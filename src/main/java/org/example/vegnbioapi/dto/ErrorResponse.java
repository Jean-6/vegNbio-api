package org.example.vegnbioapi.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data

public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

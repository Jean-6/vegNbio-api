package org.example.vegnbioapi.model;

import lombok.Data;

import java.time.LocalTime;


@Data
public class OpeningHours {
    private LocalTime open;
    private LocalTime close;
}

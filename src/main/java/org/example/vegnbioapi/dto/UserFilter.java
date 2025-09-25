package org.example.vegnbioapi.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class UserFilter {

    private List<String> status;
    private List<String>  service;
    private String name;
    private List<String> cities ;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime availabilityFrom ;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime availabilityTo;
}

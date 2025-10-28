package org.example.vegnbioapi.dto;


import lombok.Data;

import java.util.List;


@Data
public class UserFilter {

    private String status;
    private String username;
    private String email;
    private String isActive;
    private String isVerified ;
    private List<String> roles;
}

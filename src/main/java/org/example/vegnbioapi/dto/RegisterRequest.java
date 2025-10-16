package org.example.vegnbioapi.dto;


import lombok.Data;


@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String source;
    private String userType;
}

package org.example.vegnbioapi.dto;


import lombok.Data;

@Data
public class SignupResponse {
    private String username;
    private String email;
    private String password;
}

package org.example.vegnbioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String id;
    private String username;
    private String email;
    private List<Role> roles;
    private boolean isActive = true;
    //private boolean isVerified = false;
    private  String token;

}

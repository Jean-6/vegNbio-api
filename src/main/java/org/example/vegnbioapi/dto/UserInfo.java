package org.example.vegnbioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String userId;
    private String username;
    private String email;

    public UserInfo(String username,String email){
        this.username =username;
        this.email=email;
    }
}

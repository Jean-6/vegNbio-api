package org.example.vegnbioapi.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class Role {
    @Id
    private String id;
    private ERole role;

    public Role(ERole role){
        this.role = role;
    }
}

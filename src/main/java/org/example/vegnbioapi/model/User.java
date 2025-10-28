package org.example.vegnbioapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vegnbioapi.dto.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Document
@Data
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles;
    private boolean isActive = true;
    private boolean isVerified = false;
    private List<String> docs;
    private LocalDateTime createdAt;
}

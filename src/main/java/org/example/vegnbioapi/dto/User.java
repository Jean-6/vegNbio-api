package org.example.vegnbioapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Document
@Data
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @NonNull
    private String username;
    @Indexed(unique = true)
    private String email;
    @NonNull
    private String password;
    private Set<Role> roles;
    private boolean enabled = true;

    // Getters, Setters, Constructors
}

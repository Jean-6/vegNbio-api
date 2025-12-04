package org.example.vegnbioapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vegnbioapi.model.RestorerInfo;
import org.example.vegnbioapi.model.SupplierInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
    private SupplierInfo supplierInfo;
    private RestorerInfo restorerInfo;
    private LocalDateTime createdAt;

    // Getters, Setters, Constructors
}

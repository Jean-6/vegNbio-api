package org.example.vegnbioapi.model;

import lombok.Data;
import org.example.vegnbioapi.dto.ERole;
import org.example.vegnbioapi.dto.UserInfo;

import java.time.LocalDateTime;

@Data
public class RoleChangeRequest {
    private String id;
    private ERole requestedRole;// SUPPLIER ou RESTORER
    private String desc;
    private UserInfo userInfo;
    private LocalDateTime requestedAt;
    private Status status; // PENDING, APPROVED, REJECTED
    private String adminComment;
    private java.util.List<String> urlDocs;
}

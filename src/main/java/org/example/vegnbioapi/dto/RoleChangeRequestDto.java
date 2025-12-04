package org.example.vegnbioapi.dto;


import lombok.Data;
import org.example.vegnbioapi.model.Status;

import java.time.LocalDateTime;

@Data
public class RoleChangeRequestDto {
    private String uId;
    private String desc;
    private UserInfo userInfo;
    private ERole requestedRole; // SUPPLIER ou RESTORER
    private Status status;
    private String adminComment;
    private java.util.List<String> urlDocs;
    private LocalDateTime requestedAt;

}

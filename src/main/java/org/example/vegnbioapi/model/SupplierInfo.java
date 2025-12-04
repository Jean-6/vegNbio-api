package org.example.vegnbioapi.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
@NoArgsConstructor
public class SupplierInfo {
    private String companyName;
    //private String siret;
    //private String address;
    private String phoneNumber;
    //private String category;
    private List<String> docsUrl;
    private Approval approval;
    private LocalDateTime submittedAt = LocalDateTime.now();
}

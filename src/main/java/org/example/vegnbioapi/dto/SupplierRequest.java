package org.example.vegnbioapi.dto;


import lombok.Data;


@Data
public class SupplierRequest {

    private String userId;
    private String companyName;
    private String phone;
    private String address;
    private String email;
    private String desc;

}

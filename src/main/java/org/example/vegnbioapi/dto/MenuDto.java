package org.example.vegnbioapi.dto;


import lombok.Data;

@Data
public class MenuDto {
    private String id;
    private String canteenId;
    private String name;
    private String desc;
    private double price;
    private UserInfo user;
}

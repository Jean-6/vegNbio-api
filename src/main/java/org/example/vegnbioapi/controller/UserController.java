package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;



    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<List<User>>> get(
            @ModelAttribute UserFilter filters,
            HttpServletRequest request) {

        log.info(">> Load all users");

        List<User> users = userService.loadFilteredUsers(filters);
        return ResponseEntity.ok(
                ResponseWrapper.ok("User list", request.getRequestURI(), users));
    }
}

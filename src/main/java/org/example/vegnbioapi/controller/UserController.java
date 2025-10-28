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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> delete(
            @PathVariable("id") String id,
            HttpServletRequest request)  {

        log.info("id : "+ id);

        log.info(">> Delete a user ");
        User user =  userService.delete(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("User deleted", request.getRequestURI(), user));
    }

    @PostMapping("/restorer/receipt")
    public ResponseEntity<ResponseWrapper<?>> uploadReceipt(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId,
            HttpServletRequest hsr) throws IOException {

        log.info("upload restorer docs");
        List<String> urls = this.userService.saveRestorerDocs(userId,files);
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "Receipt save with sucess",
                        hsr.getRequestURI(),
                        urls
                ));
    }

    @PatchMapping(value = "/{id}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> verifyUser(@PathVariable String id, HttpServletRequest request) {
        log.info(">> Verifying user with ID: {}", id);
        User updatedUser = userService.verifyUser(id);
        return ResponseEntity.ok(
                ResponseWrapper.ok("User verified successfully", request.getRequestURI(), updatedUser));
    }

    @PatchMapping(value = "/{id}/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> toggleUserActive(@PathVariable String id, HttpServletRequest request) {
        log.info(">> Toggling active status for user with ID: {}", id);
        User updatedUser = userService.toggleActive(id);
        return ResponseEntity.ok(ResponseWrapper.ok("User active status updated", request.getRequestURI(), updatedUser));
    }
}

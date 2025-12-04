package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.RoleChangeRequestDto;
import org.example.vegnbioapi.dto.SupplierRequest;
import org.example.vegnbioapi.dto.UserFilter;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
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

    @GetMapping("/role-change-requests")
    public ResponseEntity<ResponseWrapper<List<RoleChangeRequestDto>>> ChangeRequests(
            HttpServletRequest hsr
    ) {
        List<RoleChangeRequestDto> requests = userService.getAllRoleChangeRequests();
        return ResponseEntity.ok(
                ResponseWrapper.ok("", hsr.getRequestURI(),requests));
    }


    @PostMapping(value="/become-restorer",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> becomeRestorer(
            HttpServletRequest hsr,
            Principal principal,
            @RequestPart("data") RoleChangeRequestDto requestDto,
            @RequestPart("documents") List<MultipartFile> files) {

        log.info("change status");
        User user = userService.becomeRestorer(principal,requestDto,files);
        return ResponseEntity.ok(
                ResponseWrapper.ok(
                        "Demande soumis avec succes",hsr.getRequestURI(),user
                )
        );
    }

    @PostMapping(value="/become-supplier",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> becomeSupplier(
            @RequestParam("files") List<MultipartFile> files,
            @RequestPart("data") SupplierRequest supplierRequest,
            HttpServletRequest hsr) throws IOException {


        log.info("become supplier");
        log.info(supplierRequest.toString());

        User user= userService.becomeSupplier(supplierRequest,files);
        return ResponseEntity.ok(
                ResponseWrapper.ok("Status fournisseur",hsr.getRequestURI(),user));
    }


    /*@PostMapping("/become-restorer")
    public ResponseEntity<ResponseWrapper<?>> becomeRestorer(
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
    }*/

    @PutMapping("/{id}/role-requests/approve")
    public ResponseEntity<RoleChangeRequestDto> approveRestorer(
            @PathVariable String id,
            @RequestParam(required = false) String adminComment) {
        log.info(">> Approve Role user with ID: {}", id);
        RoleChangeRequestDto updatedRequest = userService.approveRoleRequest(id, adminComment);
        return ResponseEntity.ok(updatedRequest);
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

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper<User>> delete(@PathVariable("id") String id, HttpServletRequest request)  {

                log.info("id : "+ id);
                log.info(">> Delete a user ");
                User user =  userService.delete(id);
                return ResponseEntity.ok(
                        ResponseWrapper.ok("User deleted", request.getRequestURI(), user));
    }
}

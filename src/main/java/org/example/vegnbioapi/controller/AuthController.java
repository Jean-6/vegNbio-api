package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.example.vegnbioapi.dto.LoginRequest;
import org.example.vegnbioapi.dto.LoginResponse;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.SignupRequest;
import org.example.vegnbioapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseWrapper<LoginResponse>> register(@Validated @RequestBody SignupRequest request, HttpServletRequest httpServletRequest) {
        log.info(request.toString());
        LoginResponse result = authService.register(request);
        return ResponseEntity.ok(
                ResponseWrapper.ok("registration successfully",httpServletRequest.getRequestURI(), result));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<LoginResponse>> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpServletRequest){

        log.info(request.toString());
        LoginResponse result = authService.login(request);
        return ResponseEntity.ok(
                ResponseWrapper.ok("sign-in successfully",httpServletRequest.getRequestURI(), result));
    }
}

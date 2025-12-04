package org.example.vegnbioapi.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.example.vegnbioapi.dto.LoginRequest;
import org.example.vegnbioapi.dto.LoginResponse;
import org.example.vegnbioapi.dto.ResponseWrapper;
import org.example.vegnbioapi.dto.RegisterRequest;
import org.example.vegnbioapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthService authService;

        @PostMapping("/signup")
        public ResponseEntity<ResponseWrapper<LoginResponse>> register(@Validated @RequestBody RegisterRequest request, HttpServletRequest hsr) throws BadRequestException {
            log.info(request.toString());
            LoginResponse result = authService.register(request);
            return ResponseEntity.ok(
                    ResponseWrapper.ok("registration successfully",hsr.getRequestURI(), result));
        }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseWrapper<LoginResponse>> login(@Validated @RequestBody LoginRequest request, HttpServletRequest hsr){

        log.info(request.toString());
        LoginResponse result = authService.login(request);
        return ResponseEntity.ok(
                ResponseWrapper.ok("sign-in successfully",hsr.getRequestURI(), result));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // ajouter le token dans une blacklist si nécessaire
        }
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }



}

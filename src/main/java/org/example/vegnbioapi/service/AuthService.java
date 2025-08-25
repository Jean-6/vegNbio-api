package org.example.vegnbioapi.service;


import org.example.vegnbioapi.dto.LoginRequest;
import org.example.vegnbioapi.dto.LoginResponse;
import org.example.vegnbioapi.dto.SignupRequest;

public interface AuthService {

    LoginResponse register(SignupRequest signupRequest);
    LoginResponse login(LoginRequest loginRequest);
}

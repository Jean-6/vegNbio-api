package org.example.vegnbioapi.service;


import org.apache.coyote.BadRequestException;
import org.example.vegnbioapi.dto.LoginRequest;
import org.example.vegnbioapi.dto.LoginResponse;
import org.example.vegnbioapi.dto.RegisterRequest;

public interface AuthService {

    LoginResponse register(RegisterRequest registerRequest) throws BadRequestException;
    LoginResponse login(LoginRequest loginRequest);
}

package org.example.vegnbioapi.service.imp;

import lombok.extern.slf4j.Slf4j;

import org.example.vegnbioapi.dto.*;
import org.example.vegnbioapi.model.User;
import org.example.vegnbioapi.exception.ConflictException;
import org.example.vegnbioapi.repository.UserRepository;
import org.example.vegnbioapi.security.auth.JwtUtils;
import org.example.vegnbioapi.security.auth.UserDetailsImp;
import org.example.vegnbioapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder passwordEncoder  = new BCryptPasswordEncoder();

    public LoginResponse register(SignupRequest request) {


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role defaultRole = new Role(ERole.CUSTOMER);

        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);
        UserDetails userDetails = UserDetailsImp.build(user);
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                new ArrayList<>(user.getRoles()),
                jwtToken
        );
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        log.info(" >> login method");
        log.info(loginRequest.toString());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

       // log.debug("authentication : "+ authentication.getPrincipal().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

        User user= userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        LoginResponse loginResponse= new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setRoles(new ArrayList<>(user.getRoles()));
        loginResponse.setToken(jwt);

        return loginResponse;

    }
}

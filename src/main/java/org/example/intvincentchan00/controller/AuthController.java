package org.example.intvincentchan00.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.intvincentchan00.Model.dto.request.LoginRequest;
import org.example.intvincentchan00.Model.dto.request.SignupRequest;
import org.example.intvincentchan00.Model.dto.response.LoginResponse;
import org.example.intvincentchan00.entity.User;
import org.example.intvincentchan00.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     * @param signupRequest The user registration data
     * @return user information
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) throws BadRequestException {
        User registeredUser = authService.registerUser(signupRequest);

        // create new user
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("id", registeredUser.getId());
        response.put("name", registeredUser.getName());
        response.put("email", registeredUser.getEmail());
        System.out.println("User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * login a user and generate a JWT token.
     * @param loginRequest The login request
     * @return JWT token and user info
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws BadRequestException {
        LoginResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

}
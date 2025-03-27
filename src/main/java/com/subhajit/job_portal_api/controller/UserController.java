package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.AuthenticationCredentialsRequestDTO;
import com.subhajit.job_portal_api.dto.UserSignupRequestDTO;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    // TODO:- there should be a sign out api as well to discard the present jwt token

    private final UserService userService;

    // register a user
    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserSignupRequestDTO user, @RequestParam String role){
        UserResponseDTO userResponse = userService.registerUser(user, role);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    // post a user
    @PostMapping("/auth/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody AuthenticationCredentialsRequestDTO authenticationCredentialsRequestDTO){
        String token = userService.loginUser(authenticationCredentialsRequestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body("User logged in successfully");
    }

    // get all users by certain type (employer or job_seeker)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam String role){
        List<UserResponseDTO> users = userService.getUsers(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.UserRequestDTO;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.dto.UserType;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // post a user
    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO user, @RequestParam String type){
        UserResponseDTO userResponse = userService.registerUser(user, type);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    // get all users by certain type (employer or job_seeker)
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam String type){
        List<UserResponseDTO> users = userService.getUsers(type);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

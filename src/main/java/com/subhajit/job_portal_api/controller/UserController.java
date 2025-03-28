package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.AuthenticationCredentialsRequestDTO;
import com.subhajit.job_portal_api.dto.ErrorResponseDTO;
import com.subhajit.job_portal_api.dto.UserSignupRequestDTO;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    private final UserService userService;

    @PostMapping("/auth/signup")
    @Operation(
            summary = "Register a new user",
            description = "Allows a new user to sign up with a specified role.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User signup request data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserSignupRequestDTO.class)
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "role",
                            description = "Role of the user (e.g., JOB_SEEKER, ADMIN, EMPLOYER)",
                            required = true,
                            schema = @Schema(type = "string", allowableValues = {"JOB_SEEKER", "ADMIN", "EMPLOYER"})
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User registered successfully.",
                            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Validation failed for the request body.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. A user with the given username already exists.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserSignupRequestDTO user, @RequestParam String role){
        UserResponseDTO userResponse = userService.registerUser(user, role);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user with their credentials and returns a JWT token in the Authorization header.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User authentication credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationCredentialsRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully. The JWT token is returned in the Authorization header.",
                            headers = @Header(
                                    name = HttpHeaders.AUTHORIZATION,
                                    description = "JWT token for authentication",
                                    schema = @Schema(type = "string")
                            ),
                            content = @Content(schema = @Schema(example = "User logged in successfully"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Invalid username or password.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Validation failed for the request body.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<String> loginUser(@Valid @RequestBody AuthenticationCredentialsRequestDTO authenticationCredentialsRequestDTO){
        String token = userService.loginUser(authenticationCredentialsRequestDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body("User logged in successfully");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    @Operation(
            summary = "Fetch users by role",
            description = "Retrieves a list of users based on the specified role. Only accessible to ADMIN users.",
            parameters = {
                    @Parameter(
                            name = "role",
                            description = "The role of users to fetch (e.g., JOB_SEEKER, EMPLOYER)",
                            required = true,
                            schema = @Schema(type = "string", example = "JOB_SEEKER")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users retrieved successfully",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. ADMIN users cannot fetch other ADMIN users.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid role parameter.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam String role){
        List<UserResponseDTO> users = userService.getUsers(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

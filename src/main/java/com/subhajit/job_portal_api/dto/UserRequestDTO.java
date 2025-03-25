package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @JsonProperty("user_name")
    @Size(min = 2, message = "user_name should be at least 2 characters long")
    @NotBlank(message = "user_name is mandatory")
    private String userName;
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "password is mandatory")
    private String password;
}

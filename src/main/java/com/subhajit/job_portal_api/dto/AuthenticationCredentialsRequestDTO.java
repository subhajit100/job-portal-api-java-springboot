package com.subhajit.job_portal_api.dto;

import lombok.Data;

@Data
public class AuthenticationCredentialsRequestDTO {
    private String username;
    private String password;
}

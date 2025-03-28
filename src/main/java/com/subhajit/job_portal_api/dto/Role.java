package com.subhajit.job_portal_api.dto;

import com.subhajit.job_portal_api.exception.JobPortalCustomException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum Role {
    ADMIN,
    EMPLOYER,
    JOB_SEEKER;

    public static Role from(String role) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new JobPortalCustomException("Invalid role: " + role, HttpStatus.BAD_REQUEST));
    }
}

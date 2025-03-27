package com.subhajit.job_portal_api.dto;

import java.util.Arrays;

public enum Role {
    ADMIN,
    EMPLOYER,
    JOB_SEEKER;

    public static Role from(String role) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
    }
}

package com.subhajit.job_portal_api.dto;

import java.util.Arrays;

public enum UserType {
    EMPLOYER,
    JOB_SEEKER;

    public static UserType from(String type) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid user type: " + type));
    }
}

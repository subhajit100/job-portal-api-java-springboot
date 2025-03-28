package com.subhajit.job_portal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private Integer status;
    private String message;
    private Instant timestamp;
    private String description;
}

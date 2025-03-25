package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private String message;
    @JsonProperty("time_stamp")
    private LocalDateTime timeStamp;
    private String description;
}

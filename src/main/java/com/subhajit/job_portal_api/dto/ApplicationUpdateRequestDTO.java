package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationUpdateRequestDTO {
    @JsonProperty("cover_letter")
    @NotBlank(message = "cover_letter should not be empty")
    @Size(min = 5 , message = "cover_letter should be at least 5 characters long")
    private String coverLetter;
}

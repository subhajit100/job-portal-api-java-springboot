package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationRequestDTO {
    @JsonProperty("cover_letter")
    @NotBlank(message = "cover_letter should not be empty")
    @Size(min = 5 , message = "cover_letter should be at least 5 characters long")
    private String coverLetter;

    @JsonProperty("job_id")
    @NotNull(message = "job_id should not be null")
    @Positive(message = "job_id must be positive")
    private Long appliedJobId;
}

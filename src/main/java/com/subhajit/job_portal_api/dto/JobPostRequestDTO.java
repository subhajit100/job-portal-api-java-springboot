package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobPostRequestDTO {
    @Size(min = 2, message = "title should be at least 2 characters long")
    @NotBlank(message = "title is mandatory")
    private String title;
    private String description;
    @JsonProperty("req_experience")
    private Integer reqYearsOfExp;
    private String location;
}

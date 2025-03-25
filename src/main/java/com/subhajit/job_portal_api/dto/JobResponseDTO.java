package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    @JsonProperty("req_experience")
    private Integer reqYearsOfExp;
    private String location;
    @JsonProperty("posted_date")
    private LocalDateTime postedDate;
}

package com.subhajit.job_portal_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponseDTO {
    private Long id;
    @JsonProperty("cover_letter")
    private String coverLetter;
    @JsonProperty("applied_date")
    private Instant appliedDate;
    @JsonProperty("job_id")
    private Long appliedJobId;
}

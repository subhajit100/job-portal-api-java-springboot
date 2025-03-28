package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.ErrorResponseDTO;
import com.subhajit.job_portal_api.dto.JobRequestDTO;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.service.JobService;
import com.subhajit.job_portal_api.util.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;
    private final CommonUtils commonUtils;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(
            summary = "Register a new job posting",
            description = "Allows an employer to post a new job listing.",
            requestBody = @RequestBody(
                    description = "Job details to be posted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Job posted successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JobResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Only users with the EMPLOYER role can post jobs.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employer not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User is not an employer.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<JobResponseDTO> registerJob(@Valid @RequestBody JobRequestDTO job){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        JobResponseDTO jobResponseDTO = jobService.registerJob(job, employerId);
        return new ResponseEntity<>(jobResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    @Operation(
            summary = "Get all jobs posted by an employer",
            description = "Allows an employer to retrieve all job postings they have created. If the user is an admin, they can fetch all jobs from all employers.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of jobs retrieved successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = JobResponseDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. Access restricted to EMPLOYER or ADMIN roles.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<List<JobResponseDTO>> getAllJobsPostedByEmployer(){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        List<JobResponseDTO> jobResponses = jobService.getAllJobsPostedByEmployer(employerId);
        return new ResponseEntity<>(jobResponses, HttpStatus.OK);
    }

    @PatchMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(
            summary = "Update job details",
            description = "Allows an employer to update the details of a specific job posting.",
            parameters = {
                    @Parameter(name = "jobId", description = "ID of the job to update", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated job details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Job updated successfully.",
                            content = @Content(schema = @Schema(implementation = JobResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The job does not belong to the employer.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Job or employer not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The user is not an employer.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<JobResponseDTO> updateJobById(@PathVariable Long jobId, @Valid @RequestBody JobRequestDTO jobRequestDTO){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        JobResponseDTO updatedJobResponseDTO = jobService.updateJobById(employerId, jobId, jobRequestDTO);
        return new ResponseEntity<>(updatedJobResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    @Operation(
            summary = "Delete a job posting",
            description = "Allows an employer or admin to delete a specific job posting.",
            parameters = {
                    @Parameter(name = "jobId", description = "ID of the job to delete", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Job deleted successfully.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The job does not belong to the employer.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Job not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<Object> deleteJobById(@PathVariable Long jobId){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        jobService.deleteJobById(employerId, jobId);
        return ResponseEntity.noContent().build();
    }
}

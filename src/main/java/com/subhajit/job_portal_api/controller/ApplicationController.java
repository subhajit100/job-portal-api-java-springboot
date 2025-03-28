package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.ApplicationPostRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.dto.ApplicationUpdateRequestDTO;
import com.subhajit.job_portal_api.dto.ErrorResponseDTO;
import com.subhajit.job_portal_api.service.ApplicationService;
import com.subhajit.job_portal_api.util.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CommonUtils commonUtils;


    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(
            summary = "Register a new job application",
            description = "Allows an authenticated job seeker to submit a new job application.",
            requestBody = @RequestBody(
                    description = "Job application request data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ApplicationPostRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Job application created successfully.",
                            content = @Content(schema = @Schema(implementation = ApplicationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Validation failed for the request body.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. Either the job or job seeker was not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The authenticated user is not a job seeker.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The user does not have the required role.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<ApplicationResponseDTO> registerApplication(@Valid @RequestBody ApplicationPostRequestDTO applicationRequestDTO){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        ApplicationResponseDTO applicationResponseDTO = applicationService.registerApplication(applicationRequestDTO, jobSeekerId);
        return new ResponseEntity<>(applicationResponseDTO, HttpStatus.CREATED);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN')")
    @Operation(
            summary = "Get all applications submitted by the authenticated job seeker",
            description = "Retrieves a list of all job applications submitted by the authenticated job seeker. If the user is an ADMIN, retrieves all applications.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of job applications retrieved successfully.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationResponseDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The user does not have the required role.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<List<ApplicationResponseDTO>> getAllApplicationsPostedByJobSeeker(){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        List<ApplicationResponseDTO> applicationResponses = applicationService.getAllApplicationsPostedByJobSeeker(jobSeekerId);
        return new ResponseEntity<>(applicationResponses, HttpStatus.OK);
    }


    @PatchMapping("/{applicationId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    @Operation(
            summary = "Update a job application",
            description = "Allows a job seeker to update their job application, such as modifying the cover letter.",
            parameters = {
                    @Parameter(
                            name = "applicationId",
                            description = "ID of the application to be updated",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            requestBody = @RequestBody(
                    description = "Updated application details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationUpdateRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Application updated successfully.",
                            content = @Content(schema = @Schema(implementation = ApplicationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The application does not belong to the authenticated job seeker.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Application or user not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The user is not a job seeker.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<ApplicationResponseDTO> updateApplicationById(@PathVariable Long applicationId, @Valid @RequestBody ApplicationUpdateRequestDTO applicationUpdateRequestDTO){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        ApplicationResponseDTO updatedApplicationResponseDTO = applicationService.updateApplicationById(jobSeekerId, applicationId, applicationUpdateRequestDTO);
        return new ResponseEntity<>(updatedApplicationResponseDTO, HttpStatus.OK);
    }


    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN')")
    @Operation(
            summary = "Delete a job application",
            description = "Allows a job seeker or an admin to delete a specific job application.",
            parameters = {
                    @Parameter(
                            name = "applicationId",
                            description = "ID of the application to be deleted",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Application deleted successfully.",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. The application does not belong to the authenticated job seeker.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Application not found.",
                            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            }
    )
    public ResponseEntity<Object> deleteApplicationById(@PathVariable Long applicationId){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        applicationService.deleteApplicationById(jobSeekerId, applicationId);
        return ResponseEntity.noContent().build();
    }
}

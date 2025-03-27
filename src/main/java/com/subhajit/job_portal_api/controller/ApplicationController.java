package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.ApplicationPostRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.dto.ApplicationUpdateRequestDTO;
import com.subhajit.job_portal_api.service.ApplicationService;
import com.subhajit.job_portal_api.util.CommonUtils;
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
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CommonUtils commonUtils;

    // add a new application by job_seeker , POST /api/applications/{jobSeekerId}
    @PostMapping
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponseDTO> registerApplication(@Valid @RequestBody ApplicationPostRequestDTO applicationRequestDTO){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        ApplicationResponseDTO applicationResponseDTO = applicationService.registerApplication(applicationRequestDTO, jobSeekerId);
        return new ResponseEntity<>(applicationResponseDTO, HttpStatus.CREATED);
    }

    // get all applications submitted by a job_seeker , GET /api/applications/{jobSeekerId}
    @GetMapping
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<List<ApplicationResponseDTO>> getAllApplicationsPostedByJobSeeker(){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        List<ApplicationResponseDTO> applicationResponses = applicationService.getAllApplicationsPostedByJobSeeker(jobSeekerId);
        return new ResponseEntity<>(applicationResponses, HttpStatus.OK);
    }

    // update an application by a job_seeker, PATCH /api/applications/{jobSeekerId}/{applicationId}
    @PatchMapping("/{applicationId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponseDTO> updateApplicationById(@PathVariable Long applicationId, @Valid @RequestBody ApplicationUpdateRequestDTO applicationUpdateRequestDTO){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        ApplicationResponseDTO updatedApplicationResponseDTO = applicationService.updateApplicationById(jobSeekerId, applicationId, applicationUpdateRequestDTO);
        return new ResponseEntity<>(updatedApplicationResponseDTO, HttpStatus.OK);
    }

    // delete an application by a job_seeker, DELETE /api/applications/{jobSeekerId}/{applicationId}
    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<Object> deleteApplicationById(@PathVariable Long applicationId){
        // fetch the details of the present authenticated jobSeeker and get the id of that.
        Long jobSeekerId = commonUtils.getUserIdFromAuthContext();
        applicationService.deleteApplicationById(jobSeekerId, applicationId);
        return ResponseEntity.noContent().build();
    }
}

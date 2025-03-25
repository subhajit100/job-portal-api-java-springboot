package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.ApplicationRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // TODO:- when authentication enabled then jobSeekerId is not required in path params, and will be taken from authentication context

    // add a new application by job_seeker , POST /api/applications/{jobSeekerId}
    @PostMapping("/{jobSeekerId}")
    public ResponseEntity<ApplicationResponseDTO> registerApplication(@Valid @RequestBody ApplicationRequestDTO applicationRequestDTO, @PathVariable Long jobSeekerId){
        ApplicationResponseDTO applicationResponseDTO = applicationService.registerApplication(applicationRequestDTO, jobSeekerId);
        return new ResponseEntity<>(applicationResponseDTO, HttpStatus.CREATED);
    }

    // get all applications submitted by a job_seeker , GET /api/applications/{jobSeekerId}
    @GetMapping("/{jobSeekerId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getAllApplicationsPostedByJobSeeker(@PathVariable Long jobSeekerId){
        List<ApplicationResponseDTO> applicationResponses = applicationService.getAllApplicationsPostedByJobSeeker(jobSeekerId);
        return new ResponseEntity<>(applicationResponses, HttpStatus.OK);
    }

    // update an application by a job_seeker, PATCH /api/applications/{jobSeekerId}/{applicationId}
    @PatchMapping("/{jobSeekerId}/{applicationId}")
    public ResponseEntity<ApplicationResponseDTO> updateApplicationById(@PathVariable Long jobSeekerId, @PathVariable Long applicationId, @Valid @RequestBody ApplicationRequestDTO applicationRequestDTO){
        ApplicationResponseDTO updatedApplicationResponseDTO = applicationService.updateApplicationById(jobSeekerId, applicationId, applicationRequestDTO);
        return new ResponseEntity<>(updatedApplicationResponseDTO, HttpStatus.OK);
    }

    // delete an application by a job_seeker, DELETE /api/applications/{jobSeekerId}/{applicationId}
    @DeleteMapping("/{jobSeekerId}/{applicationId}")
    public ResponseEntity<Object> deleteApplicationById(@PathVariable Long jobSeekerId, @PathVariable Long applicationId){
        applicationService.deleteApplicationById(jobSeekerId, applicationId);
        return ResponseEntity.noContent().build();
    }
}

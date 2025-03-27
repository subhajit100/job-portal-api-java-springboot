package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.JobRequestDTO;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.service.JobService;
import com.subhajit.job_portal_api.util.CommonUtils;
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
public class JobController {

    private final JobService jobService;
    private final CommonUtils commonUtils;

    // TODO:- when authentication enabled then employerId is not required in path params, and will be taken from authentication context

    // POST a new job by employer, POST /api/jobs/{employerId}
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> registerJob(@Valid @RequestBody JobRequestDTO job){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        JobResponseDTO jobResponseDTO = jobService.registerJob(job, employerId);
        return new ResponseEntity<>(jobResponseDTO, HttpStatus.CREATED);
    }

    // get all jobs posted by an employer, GET /api/jobs/{employerId}
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<List<JobResponseDTO>> getAllJobsPostedByEmployer(){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        List<JobResponseDTO> jobResponses = jobService.getAllJobsPostedByEmployer(employerId);
        return new ResponseEntity<>(jobResponses, HttpStatus.OK);
    }

    // update a job by certain employer with the jobId,  PATCH /api/jobs/{employerId}/{jobId}
    @PatchMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> updateJobById(@PathVariable Long jobId, @Valid @RequestBody JobRequestDTO jobRequestDTO){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        JobResponseDTO updatedJobResponseDTO = jobService.updateJobById(employerId, jobId, jobRequestDTO);
        return new ResponseEntity<>(updatedJobResponseDTO, HttpStatus.OK);
    }

    // delete a job by certain employer with the jobId, DELETE /api/jobs/{employerId}/{jobId}
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<Object> deleteJobById(@PathVariable Long jobId){
        // fetch the details of the present authenticated employer and get the id of that.
        Long employerId = commonUtils.getUserIdFromAuthContext();
        jobService.deleteJobById(employerId, jobId);
        return ResponseEntity.noContent().build();
    }
}

package com.subhajit.job_portal_api.controller;

import com.subhajit.job_portal_api.dto.JobRequestDTO;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // TODO:- when authentication enabled then employerId is not required in path params, and will be taken from authentication context

    // POST a new job by employer, POST /api/jobs/{employerId}
    @PostMapping("/{employerId}")
    public ResponseEntity<JobResponseDTO> registerJob(@Valid @RequestBody JobRequestDTO job, @PathVariable Long employerId){
        JobResponseDTO jobResponseDTO = jobService.registerJob(job, employerId);
        return new ResponseEntity<>(jobResponseDTO, HttpStatus.CREATED);
    }

    // get all jobs posted by an employer, GET /api/jobs/{employerId}
    @GetMapping("/{employerId}")
    public ResponseEntity<List<JobResponseDTO>> getAllJobsPostedByEmployer(@PathVariable Long employerId){
        List<JobResponseDTO> jobResponses = jobService.getAllJobsPostedByEmployer(employerId);
        return new ResponseEntity<>(jobResponses, HttpStatus.OK);
    }


    // update a job by certain employer with the jobId,  PATCH /api/jobs/{employerId}/{jobId}
    @PatchMapping("/{employerId}/{jobId}")
    public ResponseEntity<JobResponseDTO> updateJobById(@PathVariable Long employerId, @PathVariable Long jobId, @Valid @RequestBody JobRequestDTO jobRequestDTO){
        JobResponseDTO updatedJobResponseDTO = jobService.updateJobById(employerId, jobId, jobRequestDTO);
        return new ResponseEntity<>(updatedJobResponseDTO, HttpStatus.OK);
    }

    // delete a job by certain employer with the jobId, DELETE /api/jobs/{employerId}/{jobId}
    @DeleteMapping("/{employerId}/{jobId}")
    public ResponseEntity<Object> deleteJobById(@PathVariable Long employerId, @PathVariable Long jobId){
        jobService.deleteJobById(employerId, jobId);
        return ResponseEntity.noContent().build();
    }
}

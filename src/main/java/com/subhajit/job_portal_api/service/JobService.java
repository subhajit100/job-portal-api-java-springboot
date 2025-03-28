package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.JobRequestDTO;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.exception.JobPortalCustomException;
import com.subhajit.job_portal_api.model.Job;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.JobRepository;
import com.subhajit.job_portal_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    /**
     * Registers a new job posting for a given employer.
     *
     * <p>This method checks if the employer exists and has the correct role before
     * creating and saving a new job in the repository.</p>
     *
     * @param jobRequestDTO the job details provided by the employer
     * @param employerId the unique identifier of the employer posting the job
     * @return a {@link JobResponseDTO} containing the job's ID and title
     * @throws JobPortalCustomException if the employer does not exist or is not of role {@link Role#EMPLOYER}
     */
    @Transactional
    public JobResponseDTO registerJob(JobRequestDTO jobRequestDTO, Long employerId) {
        log.info("Posting a new job by the employer with id: {}", employerId);
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new JobPortalCustomException("User with id " + employerId + " not found", HttpStatus.NOT_FOUND));

        // if exists then check if it is of role EMPLOYER
        if(!employer.getRole().equals(Role.EMPLOYER)){
            throw new JobPortalCustomException("User with id " + employerId + " is not an employer", HttpStatus.CONFLICT);
        }

        // create a Job model object
        Job job = Job.builder().title(jobRequestDTO.getTitle()).description(jobRequestDTO.getDescription()).location(jobRequestDTO.getLocation()).reqYearsOfExp(jobRequestDTO.getReqYearsOfExp()).postedDate(LocalDateTime.now()).employer(employer).build();

        // save to jobRepository
        jobRepository.save(job);

        log.info("Posted the job successfully with id: {}", job.getId());
        // create a jobResponseDTO and return.
        return JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).build();
    }

    /**
     * Retrieves a list of job postings based on the employer's ID.
     *
     * <p>If an employer ID is provided, it fetches jobs posted by that specific employer.
     * If no employer ID is provided, it fetches all job postings (accessible by an admin).</p>
     *
     * @param employerId the unique identifier of the employer (optional, if null, retrieves all jobs)
     * @return a list of {@link JobResponseDTO} containing job details
     */
    public List<JobResponseDTO> getAllJobsPostedByEmployer(Long employerId) {

        List<Job> jobs;

        if(Objects.nonNull(employerId)){
            log.info("Fetching all jobs posted by employer with id: {}", employerId);
            jobs = jobRepository.findByEmployerId(employerId);
        }
        else{
            log.info("Fetching all jobs posted by employers");
            // means the authentication token is of an ADMIN
            jobs = jobRepository.findAll();
        }

        log.info("Successfully fetched {} job postings", jobs.size());
        return jobs.stream().map(job -> JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).description(job.getDescription()).location(job.getLocation()).postedDate(job.getPostedDate()).reqYearsOfExp(job.getReqYearsOfExp()).build()).toList();
    }

    /**
     * Updates an existing job posting for a given employer.
     *
     * <p>This method ensures that the employer exists, has the correct role, and owns the job before updating its details.</p>
     *
     * @param employerId the unique identifier of the employer attempting to update the job
     * @param jobId the unique identifier of the job to be updated
     * @param jobRequestDTO the updated job details provided by the employer
     * @return an updated {@link JobResponseDTO} containing the job's ID and title
     * @throws JobPortalCustomException if the employer does not exist, is not of role {@link Role#EMPLOYER},
     *         the job does not exist, or the job does not belong to the specified employer
     */
    @Transactional
    public JobResponseDTO updateJobById(Long employerId, Long jobId, JobRequestDTO jobRequestDTO) {
        log.info("Updating job posting with id: {}", jobId);
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new JobPortalCustomException("User with id " + employerId + " not found", HttpStatus.NOT_FOUND));

        // if exists then check if it is of role EMPLOYER
        if(!employer.getRole().equals(Role.EMPLOYER)){
            throw new JobPortalCustomException("User with id " + employerId + " is not an employer", HttpStatus.CONFLICT);
        }

        // check if job exists
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobPortalCustomException("Job with id " + jobId + " not found", HttpStatus.NOT_FOUND));

        // check if job is connected to the employer
        if(!job.getEmployer().getId().equals(employer.getId())){
            throw new JobPortalCustomException("Job with id " + jobId + " does not belong to employer with id " + employerId, HttpStatus.UNAUTHORIZED);
        }

        // update the job with jobRequestDTO fields
        if(Objects.nonNull(jobRequestDTO.getTitle())){
            job.setTitle(jobRequestDTO.getTitle());
        }

        if(Objects.nonNull(jobRequestDTO.getDescription())){
            job.setDescription(jobRequestDTO.getDescription());
        }

        if(Objects.nonNull(jobRequestDTO.getLocation())){
            job.setLocation(jobRequestDTO.getLocation());
        }

        if(Objects.nonNull(jobRequestDTO.getReqYearsOfExp())){
            job.setReqYearsOfExp(jobRequestDTO.getReqYearsOfExp());
        }

        // save the job to repository
        jobRepository.save(job);

        log.info("Job posting with id: {} updated successfully", jobId);
        // return the new updated jobResponseDTO
        return JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).build();
    }

    /**
     * Deletes a job posting if it belongs to the specified employer.
     *
     * <p>This method ensures that the job exists and is associated with the given employer before deleting it.</p>
     *
     * @param employerId the unique identifier of the employer attempting to delete the job
     * @param jobId the unique identifier of the job to be deleted
     * @throws JobPortalCustomException if the job does not exist or does not belong to the specified employer
     */
    @Transactional
    public void deleteJobById(Long employerId, Long jobId) {
        log.info("Deleting job posting with id: {}", jobId);

        // check if job exists
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobPortalCustomException("Job with id " + jobId + " not found", HttpStatus.NOT_FOUND));


        // check if job is connected to the authenticated employer
        if(Objects.nonNull(employerId) && !job.getEmployer().getId().equals(employerId)){
            throw new JobPortalCustomException("Job with id " + jobId + " does not belong to employer with id " + employerId, HttpStatus.UNAUTHORIZED);
        }

        jobRepository.delete(job);
        log.info("Successfully deleted job posting with id: {}", jobId);
    }
}

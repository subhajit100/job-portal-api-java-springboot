package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.JobRequestDTO;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.dto.UserType;
import com.subhajit.job_portal_api.exception.JobNotFoundException;
import com.subhajit.job_portal_api.exception.UnauthorizedAccessException;
import com.subhajit.job_portal_api.exception.UserNotFoundException;
import com.subhajit.job_portal_api.model.Job;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.JobRepository;
import com.subhajit.job_portal_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class JobService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    // TODO:- add log statements to start and end of every service method

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobResponseDTO registerJob(JobRequestDTO jobRequestDTO, Long employerId) {
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new UserNotFoundException("User with id " + employerId + " not found"));

        // if exists then check if it is of userType EMPLOYER
        if(!employer.getUserType().equals(UserType.EMPLOYER)){
            throw new IllegalArgumentException("User with id " + employerId + " is not an employer");
        }

        // create a Job model object
        Job job = Job.builder().title(jobRequestDTO.getTitle()).description(jobRequestDTO.getDescription()).location(jobRequestDTO.getLocation()).reqYearsOfExp(jobRequestDTO.getReqYearsOfExp()).postedDate(LocalDateTime.now()).employer(employer).build();

        // save to jobRepository
        jobRepository.save(job);

        // create a jobResponseDTO and return.
        return JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).build();
    }

    public List<JobResponseDTO> getAllJobsPostedByEmployer(Long employerId) {
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new UserNotFoundException("User with id " + employerId + " not found"));

        // if exists then check if it is of userType EMPLOYER
        if(!employer.getUserType().equals(UserType.EMPLOYER)){
            throw new IllegalArgumentException("User with id " + employerId + " is not an employer");
        }

        List<Job> jobs = jobRepository.findByEmployerId(employerId);

        return jobs.stream().map(job -> JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).description(job.getDescription()).location(job.getLocation()).postedDate(job.getPostedDate()).reqYearsOfExp(job.getReqYearsOfExp()).build()).toList();
    }

    @Transactional
    public JobResponseDTO updateJobById(Long employerId, Long jobId, JobRequestDTO jobRequestDTO) {
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new UserNotFoundException("User with id " + employerId + " not found"));

        // if exists then check if it is of userType EMPLOYER
        if(!employer.getUserType().equals(UserType.EMPLOYER)){
            throw new IllegalArgumentException("User with id " + employerId + " is not an employer");
        }

        // check if job exists
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException("Job with id " + jobId + " not found"));

        // check if job is connected to the employer
        if(!job.getEmployer().getId().equals(employer.getId())){
            throw new UnauthorizedAccessException("Job with id " + jobId + " does not belong to employer with id " + employerId);
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

        // return the new updated jobResponseDTO
        return JobResponseDTO.builder().id(job.getId()).title(job.getTitle()).build();
    }

    @Transactional
    public void deleteJobById(Long employerId, Long jobId) {
        // check if employer exists
        User employer = userRepository.findById(employerId).orElseThrow(() -> new UserNotFoundException("User with id " + employerId + " not found"));

        // if exists then check if it is of userType EMPLOYER
        if(!employer.getUserType().equals(UserType.EMPLOYER)){
            throw new IllegalArgumentException("User with id " + employerId + " is not an employer");
        }

        // check if job exists
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException("Job with id " + jobId + " not found"));

        // check if job is connected to the employer
        if(!job.getEmployer().getId().equals(employer.getId())){
            throw new UnauthorizedAccessException("Job with id " + jobId + " does not belong to employer with id " + employerId);
        }

        jobRepository.delete(job);
    }
}

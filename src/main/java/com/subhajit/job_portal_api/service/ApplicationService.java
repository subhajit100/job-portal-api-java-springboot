package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.ApplicationRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.dto.UserType;
import com.subhajit.job_portal_api.exception.ApplicationNotFoundException;
import com.subhajit.job_portal_api.exception.JobNotFoundException;
import com.subhajit.job_portal_api.exception.UnauthorizedAccessException;
import com.subhajit.job_portal_api.exception.UserNotFoundException;
import com.subhajit.job_portal_api.model.Application;
import com.subhajit.job_portal_api.model.Job;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.ApplicationRepository;
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
public class ApplicationService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public ApplicationResponseDTO registerApplication(ApplicationRequestDTO applicationRequestDTO, Long jobSeekerId) {
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new UserNotFoundException("User with id " + jobSeekerId + " not found"));

        // if exists then check if it is of userType JOB_SEEKER
        if(!jobSeeker.getUserType().equals(UserType.JOB_SEEKER)){
            throw new IllegalArgumentException("User with id " + jobSeekerId + " is not a job_seeker");
        }

        // check if job id exists or not?
        Job job = jobRepository.findById(applicationRequestDTO.getAppliedJobId()).orElseThrow(() -> new JobNotFoundException("Job with id " + applicationRequestDTO.getAppliedJobId() + " not found"));

        // create an application
        Application application = Application.builder().coverLetter(applicationRequestDTO.getCoverLetter()).appliedDate(LocalDateTime.now()).applicant(jobSeeker).job(job).build();

        // save the application to the db
        applicationRepository.save(application);

        // return the application response dto
        return ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedDate(application.getAppliedDate()).appliedJobId(application.getJob().getId()).build();
    }

    public List<ApplicationResponseDTO> getAllApplicationsPostedByJobSeeker(Long jobSeekerId) {
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new UserNotFoundException("User with id " + jobSeekerId + " not found"));

        // if exists then check if it is of userType JOB_SEEKER
        if(!jobSeeker.getUserType().equals(UserType.JOB_SEEKER)){
            throw new IllegalArgumentException("User with id " + jobSeekerId + " is not a job_seeker");
        }

        // find all applications submitted by the jobSeeker
        List<Application> applications = applicationRepository.findByApplicantId(jobSeekerId);

        // return them in application response dto format.
        return applications.stream().map(application -> ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedDate(application.getAppliedDate()).appliedJobId(application.getJob().getId()).build()).toList();
    }

    @Transactional
    public ApplicationResponseDTO updateApplicationById(Long jobSeekerId, Long applicationId,ApplicationRequestDTO applicationRequestDTO) {
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new UserNotFoundException("User with id " + jobSeekerId + " not found"));

        // if exists then check if it is of userType JOB_SEEKER
        if(!jobSeeker.getUserType().equals(UserType.JOB_SEEKER)){
            throw new IllegalArgumentException("User with id " + jobSeekerId + " is not a job_seeker");
        }

        // check if application with applicationId exists
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new ApplicationNotFoundException("Application with id " + applicationId + " not found"));

        // check if the jobSeeker has only posted this application
        if(!application.getApplicant().getId().equals(jobSeeker.getId())){
            throw new UnauthorizedAccessException("Application with id " + applicationId + " does not belong to job seeker with id " + jobSeekerId);
        }

        // update the application with request dto object
        if(Objects.nonNull(applicationRequestDTO.getCoverLetter())){
            application.setCoverLetter(applicationRequestDTO.getCoverLetter());
        }

        // save the updated application to db
        applicationRepository.save(application);

        // return the application response dto
        return ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedJobId(application.getJob().getId()).appliedDate(application.getAppliedDate()).build();
    }

    @Transactional
    public void deleteApplicationById(Long jobSeekerId, Long applicationId) {
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new UserNotFoundException("User with id " + jobSeekerId + " not found"));

        // if exists then check if it is of userType JOB_SEEKER
        if(!jobSeeker.getUserType().equals(UserType.JOB_SEEKER)){
            throw new IllegalArgumentException("User with id " + jobSeekerId + " is not a job_seeker");
        }

        // check if application with applicationId exists
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new ApplicationNotFoundException("Application with id " + applicationId + " not found"));

        // check if the jobSeeker has only posted this application
        if(!application.getApplicant().getId().equals(jobSeeker.getId())){
            throw new UnauthorizedAccessException("Application with id " + applicationId + " does not belong to job seeker with id " + jobSeekerId);
        }

        // delete the application by id
        applicationRepository.delete(application);
    }
}

package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.ApplicationPostRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.dto.ApplicationUpdateRequestDTO;
import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.exception.*;
import com.subhajit.job_portal_api.model.Application;
import com.subhajit.job_portal_api.model.Job;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.ApplicationRepository;
import com.subhajit.job_portal_api.repository.JobRepository;
import com.subhajit.job_portal_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    /**
     * Registers a new job application for a given jobSeeker.
     *
     * <p>This method ensures that the jobSeeker exists, has the correct role, and that the job being applied for exists before creating an application.</p>
     *
     * @param applicationPostRequestDTO the request data containing the job ID and cover letter
     * @param jobSeekerId the unique identifier of the jobSeeker submitting the application
     * @return an {@link ApplicationResponseDTO} containing the application ID, cover letter, applied job ID, and applied date
     * @throws JobPortalCustomException if the jobSeeker does not exist, is not of role {@link Role#JOB_SEEKER},
     *         or if the job being applied for does not exist
     */
    @Transactional
    public ApplicationResponseDTO registerApplication(ApplicationPostRequestDTO applicationPostRequestDTO, Long jobSeekerId) {
        log.info("Creating a new application for a job posting by jobSeeker with id: {}", jobSeekerId);
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new JobPortalCustomException("User with id " + jobSeekerId + " not found", HttpStatus.NOT_FOUND));

        // if exists then check if it is of role
        if(!jobSeeker.getRole().equals(Role.JOB_SEEKER)){
            throw new JobPortalCustomException("User with id " + jobSeekerId + " is not a job_seeker", HttpStatus.CONFLICT);
        }

        // check if job id exists or not?
        Job job = jobRepository.findById(applicationPostRequestDTO.getAppliedJobId()).orElseThrow(() -> new JobPortalCustomException("Job with id " + applicationPostRequestDTO.getAppliedJobId() + " not found", HttpStatus.NOT_FOUND));

        // create an application
        Application application = Application.builder().coverLetter(applicationPostRequestDTO.getCoverLetter()).appliedDate(LocalDateTime.now()).applicant(jobSeeker).job(job).build();

        // save the application to the db
        applicationRepository.save(application);

        log.info("Created an application successfully with id: {}", application.getId());
        // return the application response dto
        return ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedDate(application.getAppliedDate()).appliedJobId(application.getJob().getId()).build();
    }

    /**
     * Retrieves all job applications submitted by a specific jobSeeker.
     *
     * <p>If a jobSeeker ID is provided, this method fetches only the applications submitted by that jobSeeker. If no ID is provided, it fetches all applications, assuming the request is made by an admin.</p>
     *
     * @param jobSeekerId the unique identifier of the jobSeeker whose applications are being retrieved (nullable for admin access)
     * @return a list of {@link ApplicationResponseDTO} containing details of each application
     * @throws JobPortalCustomException if no applications are found for the given jobSeeker ID
     */
    public List<ApplicationResponseDTO> getAllApplicationsPostedByJobSeeker(Long jobSeekerId) {
        List<Application> applications;
        if(Objects.nonNull(jobSeekerId)){
            log.info("Fetching all applications submitted by jobSeeker with id: {}", jobSeekerId);
            // find all applications submitted by the jobSeeker
            applications = applicationRepository.findByApplicantId(jobSeekerId);
        }
        else{
            log.info("Fetching all applications submitted by jobSeekers");
            // means the authentication token is of an ADMIN
            applications = applicationRepository.findAll();
        }

        log.info("Successfully fetched {} applications", applications.size());
        // return them in application response dto format.
        return applications.stream().map(application -> ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedDate(application.getAppliedDate()).appliedJobId(application.getJob().getId()).build()).toList();
    }

    /**
     * Updates an existing job application submitted by a jobSeeker.
     *
     * <p>This method checks if the jobSeeker exists and has the correct role. It also verifies that the application exists and belongs to the specified jobSeeker before updating the cover letter.</p>
     *
     * @param jobSeekerId the unique identifier of the jobSeeker attempting to update the application
     * @param applicationId the unique identifier of the application to be updated
     * @param applicationUpdateRequestDTO an object containing the new cover letter details for the application
     * @return an {@link ApplicationResponseDTO} containing the updated application details
     * @throws JobPortalCustomException if the jobSeeker does not exist, is not a jobSeeker, the application does not exist, or the application does not belong to the jobSeeker
     */
    @Transactional
    public ApplicationResponseDTO updateApplicationById(Long jobSeekerId, Long applicationId, ApplicationUpdateRequestDTO applicationUpdateRequestDTO) {
        log.info("Updating application with id: {}", applicationId);
        // find if a jobSeeker exists with the jobSeekerId
        User jobSeeker = userRepository.findById(jobSeekerId).orElseThrow(() -> new JobPortalCustomException("User with id " + jobSeekerId + " not found", HttpStatus.NOT_FOUND));

        // if exists then check if it is of role
        if(!jobSeeker.getRole().equals(Role.JOB_SEEKER)){
            throw new JobPortalCustomException("User with id " + jobSeekerId + " is not a job_seeker", HttpStatus.CONFLICT);
        }

        // check if application with applicationId exists
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new JobPortalCustomException("Application with id " + applicationId + " not found", HttpStatus.NOT_FOUND));

        // check if the jobSeeker has only posted this application
        if(!application.getApplicant().getId().equals(jobSeeker.getId())){
            throw new JobPortalCustomException("Application with id " + applicationId + " does not belong to job seeker with id " + jobSeekerId, HttpStatus.FORBIDDEN);
        }

        // update the application with request dto object
        if(Objects.nonNull(applicationUpdateRequestDTO.getCoverLetter())){
            application.setCoverLetter(applicationUpdateRequestDTO.getCoverLetter());
        }

        // save the updated application to db
        applicationRepository.save(application);

        log.info("Application with id: {} updated successfully", applicationId);

        // return the application response dto
        return ApplicationResponseDTO.builder().id(application.getId()).coverLetter(application.getCoverLetter()).appliedJobId(application.getJob().getId()).appliedDate(application.getAppliedDate()).build();
    }

    /**
     * Deletes a job application submitted by a jobSeeker.
     *
     * <p>This method verifies the existence of the application and checks if the provided jobSeeker ID matches the applicant before allowing deletion.</p>
     *
     * @param jobSeekerId the unique identifier of the jobSeeker attempting to delete the application
     * @param applicationId the unique identifier of the application to be deleted
     * @throws JobPortalCustomException if the application does not exist or if the application does not belong to the specified jobSeeker
     */
    @Transactional
    public void deleteApplicationById(Long jobSeekerId, Long applicationId) {
        // check if application with applicationId exists
        log.info("Deleting application with id: {}", applicationId);
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new JobPortalCustomException("Application with id " + applicationId + " not found", HttpStatus.NOT_FOUND));

        // check if the jobSeeker has only posted this application
        if(Objects.nonNull(jobSeekerId) && !application.getApplicant().getId().equals(jobSeekerId)){
            throw new JobPortalCustomException("Application with id " + applicationId + " does not belong to job seeker with id " + jobSeekerId, HttpStatus.FORBIDDEN);
        }

        // delete the application
        applicationRepository.delete(application);

        log.info("Successfully deleted application with id: {}", applicationId);
    }
}

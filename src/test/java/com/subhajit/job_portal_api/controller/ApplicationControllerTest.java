package com.subhajit.job_portal_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhajit.job_portal_api.dto.ApplicationPostRequestDTO;
import com.subhajit.job_portal_api.dto.ApplicationResponseDTO;
import com.subhajit.job_portal_api.dto.ApplicationUpdateRequestDTO;
import com.subhajit.job_portal_api.filter.JwtAuthFilter;
import com.subhajit.job_portal_api.service.ApplicationService;
import com.subhajit.job_portal_api.util.CommonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@ExtendWith(MockitoExtension.class)  // Enables Mockito extension
@AutoConfigureMockMvc(addFilters = false)
public class ApplicationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationService applicationService; // Mocked service

    @MockitoBean
    private CommonUtils commonUtils; // Mocked utility class

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @WithMockUser(roles = "JOB_SEEKER")
    void registerApplication_ShouldReturnCreated() throws Exception {
        // Creating a valid request DTO
        ApplicationPostRequestDTO requestDTO = new ApplicationPostRequestDTO();
        requestDTO.setCoverLetter("This is my cover letter.");
        requestDTO.setAppliedJobId(1L);

        ApplicationResponseDTO responseDTO = new ApplicationResponseDTO();

        when(commonUtils.getUserIdFromAuthContext()).thenReturn(1L);
        when(applicationService.registerApplication(any(ApplicationPostRequestDTO.class), anyLong()))
                .thenReturn(responseDTO);

        // Convert requestDTO to JSON using ObjectMapper
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "JOB_SEEKER")
    void getAllApplicationsPostedByJobSeeker_ShouldReturnOk() throws Exception {
        List<ApplicationResponseDTO> applications = Collections.singletonList(new ApplicationResponseDTO());

        when(commonUtils.getUserIdFromAuthContext()).thenReturn(1L);
        when(applicationService.getAllApplicationsPostedByJobSeeker(anyLong()))
                .thenReturn(applications);

        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "JOB_SEEKER")
    void updateApplicationById_ShouldReturnOk() throws Exception {
        ApplicationUpdateRequestDTO requestDTO = new ApplicationUpdateRequestDTO();
        requestDTO.setCoverLetter("This is my cover letter.");
        ApplicationResponseDTO responseDTO = ApplicationResponseDTO.builder().coverLetter("This is my updated cover letter").appliedDate(Instant.now()).appliedJobId(1L).build();

        when(commonUtils.getUserIdFromAuthContext()).thenReturn(1L);
        when(applicationService.updateApplicationById(anyLong(), anyLong(), any(ApplicationUpdateRequestDTO.class)))
                .thenReturn(responseDTO);

        // Convert requestDTO to JSON using ObjectMapper
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(patch("/api/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cover_letter").value("This is my updated cover letter"))
                .andExpect(jsonPath("$.job_id").value(1));
    }

    @Test
    @WithMockUser(roles = "JOB_SEEKER")
    void deleteApplicationById_ShouldReturnNoContent() throws Exception {
        when(commonUtils.getUserIdFromAuthContext()).thenReturn(1L);

        mockMvc.perform(delete("/api/applications/1"))
                .andExpect(status().isNoContent());
    }
}

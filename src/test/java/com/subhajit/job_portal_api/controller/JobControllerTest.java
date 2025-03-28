package com.subhajit.job_portal_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhajit.job_portal_api.dto.JobResponseDTO;
import com.subhajit.job_portal_api.filter.JwtAuthFilter;
import com.subhajit.job_portal_api.repository.UserRepository;
import com.subhajit.job_portal_api.service.JobService;
import com.subhajit.job_portal_api.util.CommonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(JobController.class)
public class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommonUtils commonUtils;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private List<JobResponseDTO> mockJobs;

    @BeforeEach
    void setUp() {
        mockJobs = List.of(
                JobResponseDTO.builder().id(1L).title("Software Engineer").description("Develop applications").location("Remote").reqYearsOfExp(2).build(),
                JobResponseDTO.builder().id(2L).title("Backend Developer").description("Work on APIs").location("Onsite").reqYearsOfExp(3).build()
        );
        when(commonUtils.getUserIdFromAuthContext()).thenReturn(1L);
    }

    @Test
    @WithMockUser(roles = "EMPLOYER") // Mock authentication with EMPLOYER role
    void shouldReturnJobsForEmployer() throws Exception {
        when(jobService.getAllJobsPostedByEmployer(Mockito.anyLong())).thenReturn(mockJobs);

        mockMvc.perform(get("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockJobs.size()))
                .andExpect(jsonPath("$[0].title").value("Software Engineer"))
                .andExpect(jsonPath("$[1].title").value("Backend Developer"));
    }

    @Test
    @WithMockUser(roles = "USER") // Unauthorized role
    void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedForNoAuth() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isUnauthorized());
    }
}

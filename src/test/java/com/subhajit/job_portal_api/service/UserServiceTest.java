package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.dto.UserSignupRequestDTO;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.UserRepository;
import com.subhajit.job_portal_api.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    User employer;
    User jobSeeker;
    UserSignupRequestDTO userSignupRequestDTO;

    @BeforeEach
    void setUp() {
        employer = User.builder().email("asubh@gm.coo").username("Subho").password("spass").role(Role.EMPLOYER).build();
//        userService = new UserService(userRepository, passwordEncoder, authenticationManager, jwtUtil);
        userSignupRequestDTO = new UserSignupRequestDTO();
        userSignupRequestDTO.setEmail(employer.getEmail());
        userSignupRequestDTO.setPassword(employer.getPassword());
        userSignupRequestDTO.setUsername(employer.getUsername());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_successfullyRegistersUser(){
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        // Mock behavior: Simulate saving the user (without actual DB call)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Simulate DB-generated ID
            return savedUser;
        });
        UserResponseDTO userResponseDTO = userService.registerUser(userSignupRequestDTO, Role.EMPLOYER.name());
        assertThat(userResponseDTO.getEmail()).isEqualTo("asubh@gm.coo");
        assertThat(userResponseDTO.getUsername()).isEqualTo("Subho");
        assertThat(userResponseDTO.getRole()).isEqualTo(Role.EMPLOYER);
        assertThat(userResponseDTO.getId()).isEqualTo(1L);

        // Ensure save() was actually called
        verify(userRepository, times(1)).save(any(User.class));
    }
}

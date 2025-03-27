package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.AuthenticationCredentialsRequestDTO;
import com.subhajit.job_portal_api.dto.UserSignupRequestDTO;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.exception.JobPortalCustomException;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.UserRepository;
import com.subhajit.job_portal_api.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponseDTO registerUser(UserSignupRequestDTO userRequestDTO, String role) {
        // extract the role from request params
        Role roleType = Role.from(role);

        // check if user already exists
        if(userRepository.existsByUsername(userRequestDTO.getUsername())){
            throw new JobPortalCustomException("User already exists", HttpStatus.CONFLICT);
        }


        // creating user to add to repo
        User user = User.builder().username(userRequestDTO.getUsername()).email(userRequestDTO.getEmail()).role(roleType).password(passwordEncoder.encode(userRequestDTO.getPassword())).build();

        // adding to repository
        userRepository.save(user);

        logger.info("Created user with id: {} of type {}", user.getId(), user.getRole());

        // creating the response dto object
        return UserResponseDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).role(roleType).build();
    }


    public String loginUser(@Valid AuthenticationCredentialsRequestDTO authCredentialsRequestDTO) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authCredentialsRequestDTO.getUsername(), authCredentialsRequestDTO.getPassword()));

            // this will call the loadUserByUsername method from UserServiceDetailsImpl class, and get the userDetails from the db with help of username
            User user = (User) authenticate.getPrincipal();
            user.setPassword(null);
            return jwtUtil.generateToken(user);
        } catch (BadCredentialsException e) {
            throw new JobPortalCustomException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    public List<UserResponseDTO> getUsers(String role) {
        // extract the role from request params
        Role roleType = Role.from(role);

        if(roleType.equals(Role.ADMIN)){
            throw new JobPortalCustomException(roleType.name() + " cannot fetch other " + roleType.name() + " details , please select some other role to fetch", HttpStatus.UNAUTHORIZED);
        }

        // get all users of the above role (employer/job_seeker)
        List<User> users = userRepository.findByRole(roleType);

        logger.info("Found {} users of type {}", users.size(), roleType);

        return users.stream().map(user -> UserResponseDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).build()).toList();
    }
}

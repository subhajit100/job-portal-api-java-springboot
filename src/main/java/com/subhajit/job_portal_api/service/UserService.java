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
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user with the specified role.
     *
     * <p>This method checks if the username already exists. If not, it creates a new user
     * with the provided details, encodes the password, saves the user to the repository, and returns
     * a {@link UserResponseDTO} with the user's details.</p>
     *
     * @param userRequestDTO the user details for registration, including username, email, and password
     * @param role the role of the user (e.g., JOB_SEEKER, EMPLOYER)
     * @return a {@link UserResponseDTO} containing the newly registered user's details
     * @throws JobPortalCustomException if the username already exists
     */
    @Transactional
    public UserResponseDTO registerUser(UserSignupRequestDTO userRequestDTO, String role) {
        // extract the role from request params
        Role roleType = Role.from(role);

        log.info("Adding a new user with username: {} and role: {}", userRequestDTO.getUsername(), roleType.name());

        // check if user already exists
        if(userRepository.existsByUsername(userRequestDTO.getUsername())){
            throw new JobPortalCustomException("User already exists", HttpStatus.CONFLICT);
        }


        // creating user to add to repo
        User user = User.builder().username(userRequestDTO.getUsername()).email(userRequestDTO.getEmail()).role(roleType).password(passwordEncoder.encode(userRequestDTO.getPassword())).build();

        // adding to repository
        userRepository.save(user);

        log.info("Successfully created a new user with id: {} of type {}", user.getId(), user.getRole().name());

        // creating the response dto object
        return UserResponseDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).role(roleType).build();
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * <p>This method attempts to authenticate the user using the provided credentials.
     * If authentication is successful, a JWT token is generated and returned.</p>
     *
     * @param authCredentialsRequestDTO the user's authentication credentials containing username and password
     * @return a JWT token as a {@link String} if authentication is successful
     * @throws JobPortalCustomException if the credentials are invalid
     */
    public String loginUser(@Valid AuthenticationCredentialsRequestDTO authCredentialsRequestDTO) {
        try {
            log.info("Trying to login for a user with name: {}", authCredentialsRequestDTO.getUsername());
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authCredentialsRequestDTO.getUsername(), authCredentialsRequestDTO.getPassword()));

            // this will call the loadUserByUsername method from UserServiceDetailsImpl class, and get the userDetails from the db with help of username
            User user = (User) authenticate.getPrincipal();
            user.setPassword(null);
            log.info("Login successful for username: {}", user.getUsername());
            return jwtUtil.generateToken(user);
        } catch (BadCredentialsException e) {
            throw new JobPortalCustomException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Retrieves a list of users based on their role.
     *
     * <p>This method filters users by the specified role and returns a list of user details.
     * Admin users are restricted from fetching other admin details.</p>
     *
     * @param role the role of users to fetch (e.g., "EMPLOYER" or "JOB_SEEKER")
     * @return a list of {@link UserResponseDTO} objects containing user details
     * @throws JobPortalCustomException if an admin attempts to fetch other admin details or if the role is invalid
     */
    public List<UserResponseDTO> getUsers(String role) {
        // extract the role from request params
        Role roleType = Role.from(role);

        log.info("Fetching all users with role: {}", roleType.name());
        if(roleType.equals(Role.ADMIN)){
            throw new JobPortalCustomException(roleType.name() + " cannot fetch other " + roleType.name() + " details , please select some other role to fetch", HttpStatus.FORBIDDEN);
        }

        // get all users of the above role (employer/job_seeker)
        List<User> users = userRepository.findByRole(roleType);

        log.info("Found {} users of type {}", users.size(), roleType.name());

        return users.stream().map(user -> UserResponseDTO.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).build()).toList();
    }
}

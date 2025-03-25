package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.dto.UserRequestDTO;
import com.subhajit.job_portal_api.dto.UserResponseDTO;
import com.subhajit.job_portal_api.dto.UserType;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO, String type) {
        // extract the UserType from type String
        UserType userType = UserType.from(type);

        // creating user to add to repo
        User user = User.builder().userName(userRequestDTO.getUserName()).email(userRequestDTO.getEmail()).userType(userType).password(userRequestDTO.getPassword()).build();

        // adding to repository
        userRepository.save(user);

        logger.info("Created user with id: {} of type {}", user.getId(), user.getUserType());

        // creating the response dto object
        return UserResponseDTO.builder().id(user.getId()).userName(user.getUserName()).email(user.getEmail()).userType(userType).build();
    }

    public List<UserResponseDTO> getUsers(String type) {
        // extract the UserType from type String
        UserType userType = UserType.from(type);

        // get all users of the above userType (employer/job_seeker)
        List<User> users = userRepository.findByUserType(userType);

        logger.info("Found {} users of type {}", users.size(), userType);

        return users.stream().map(user -> UserResponseDTO.builder().id(user.getId()).userName(user.getUserName()).email(user.getEmail()).build()).toList();
    }
}

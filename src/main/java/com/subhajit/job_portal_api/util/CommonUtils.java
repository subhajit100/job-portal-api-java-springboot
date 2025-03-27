package com.subhajit.job_portal_api.util;

import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.model.User;
import com.subhajit.job_portal_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CommonUtils {
    private final UserRepository userRepository;

    public Long getUserIdFromAuthContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Subhajit: fetched username from auth context " + username);
        return getUserIdFromUsername(username);
    }

    private Long getUserIdFromUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return Objects.nonNull(user) && !user.getRole().equals(Role.ADMIN) ? user.getId() : null;
    }
}

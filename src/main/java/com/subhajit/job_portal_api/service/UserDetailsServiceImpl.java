package com.subhajit.job_portal_api.service;

import com.subhajit.job_portal_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        // fetch the user by username
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }
}

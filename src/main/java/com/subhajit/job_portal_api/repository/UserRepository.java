package com.subhajit.job_portal_api.repository;

import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

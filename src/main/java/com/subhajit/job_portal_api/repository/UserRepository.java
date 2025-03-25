package com.subhajit.job_portal_api.repository;

import com.subhajit.job_portal_api.dto.UserType;
import com.subhajit.job_portal_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserType(UserType userType);
}

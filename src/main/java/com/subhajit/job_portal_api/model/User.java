package com.subhajit.job_portal_api.model;

import com.subhajit.job_portal_api.dto.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")  // primary reason for change is:- user table already present in H2 db which conflicts with this user table of ours
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING) // this will add enum in string format instead of ordinal.
    private UserType userType;

    @OneToMany(mappedBy = "applicant")  // this will search for applicant java field in Application class
    @Builder.Default
    List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "employer")  // this will search for employer java field in Job class
    @Builder.Default
    List<Job> jobPostings = new ArrayList<>();
}

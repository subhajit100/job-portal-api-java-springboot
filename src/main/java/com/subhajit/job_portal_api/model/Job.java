package com.subhajit.job_portal_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int reqYearsOfExp;
    private String location;
    private Instant postedDate;

    @ManyToOne
    @JoinColumn(name = "employer_id") // for db, employer_id will be shown
    private User employer;

    @OneToMany(mappedBy = "job")
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}

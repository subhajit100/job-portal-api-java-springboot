package com.subhajit.job_portal_api.repository;

import com.subhajit.job_portal_api.dto.Role;
import com.subhajit.job_portal_api.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    User employer;
    User jobSeeker;

    @BeforeEach
    void setUp() {
        // insert some job records into h2 db.
        employer = User.builder().email("asubh@gm.coo").username("Subho").password("spass").role(Role.EMPLOYER).build();
        jobSeeker = User.builder().email("anand@gm.coo").username("anand").password("apass").role(Role.JOB_SEEKER).build();
        userRepository.save(employer);
        userRepository.save(jobSeeker);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        employer = null;
        jobSeeker = null;
    }

    @Test
    public void testFindByEmployerId_returnsProperData(){
        List<User> users = userRepository.findByRole(Role.EMPLOYER);
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getId()).isEqualTo(1L);
        assertThat(users.get(0).getUsername()).isNotEqualTo("anand");
    }

}

package com.certifolio.server.repository;

import com.certifolio.server.domain.Education;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findAllByUser(User user);
}

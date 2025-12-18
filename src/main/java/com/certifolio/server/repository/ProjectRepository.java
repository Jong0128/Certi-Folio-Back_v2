package com.certifolio.server.repository;

import com.certifolio.server.domain.Project;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByUser(User user);
}

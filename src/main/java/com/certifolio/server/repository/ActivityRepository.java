package com.certifolio.server.repository;

import com.certifolio.server.domain.Activity;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByUser(User user);
}

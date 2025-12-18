package com.certifolio.server.repository;

import com.certifolio.server.domain.Career;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findAllByUser(User user);
}

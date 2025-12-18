package com.certifolio.server.repository;

import com.certifolio.server.domain.Certificate;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByUser(User user);
    List<Certificate> findAllByUserId(Long userId);
}

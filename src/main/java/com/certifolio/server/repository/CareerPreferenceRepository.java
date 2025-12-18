package com.certifolio.server.repository;

import com.certifolio.server.domain.CareerPreference;
import com.certifolio.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CareerPreferenceRepository extends JpaRepository<CareerPreference, Long> {
    Optional<CareerPreference> findByUser(User user);
}

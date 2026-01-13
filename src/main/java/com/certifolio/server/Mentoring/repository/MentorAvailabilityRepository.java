package com.certifolio.server.Mentoring.repository;

import com.certifolio.server.Mentoring.domain.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {

    List<MentorAvailability> findByMentorId(Long mentorId);

    void deleteByMentorId(Long mentorId);
}

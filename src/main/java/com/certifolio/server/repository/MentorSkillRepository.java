package com.certifolio.server.repository;

import com.certifolio.server.domain.MentorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorSkillRepository extends JpaRepository<MentorSkill, Long> {

    List<MentorSkill> findByMentorId(Long mentorId);

    void deleteByMentorId(Long mentorId);
}

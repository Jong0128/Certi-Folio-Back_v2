package com.certifolio.server.Mentoring.repository;

import com.certifolio.server.Mentoring.domain.Mentor;
import com.certifolio.server.Mentoring.domain.MentorStatus;
import com.certifolio.server.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Optional<Mentor> findByUser(User user);

    Optional<Mentor> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<Mentor> findByStatus(MentorStatus status);

    @Query("SELECT m FROM Mentor m WHERE m.status = :status ORDER BY m.rating DESC")
    List<Mentor> findByStatusOrderByRatingDesc(@Param("status") MentorStatus status);

    @Query("SELECT m FROM Mentor m JOIN m.skills s WHERE s.skillName IN :skills AND m.status = 'APPROVED'")
    List<Mentor> findBySkillsContaining(@Param("skills") List<String> skills);

    @Query("SELECT m FROM Mentor m WHERE m.location = :location AND m.status = 'APPROVED'")
    List<Mentor> findByLocation(@Param("location") String location);

    @Query("SELECT m FROM Mentor m WHERE m.status = 'APPROVED' ORDER BY m.rating DESC, m.reviewCount DESC")
    List<Mentor> findTopMentors();
}

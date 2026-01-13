package com.certifolio.server.Mentoring.repository;

import com.certifolio.server.Mentoring.domain.MentorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorReviewRepository extends JpaRepository<MentorReview, Long> {

    List<MentorReview> findByMentorIdOrderByCreatedAtDesc(Long mentorId);

    @Query("SELECT AVG(r.rating) FROM MentorReview r WHERE r.mentor.id = :mentorId")
    Double calculateAverageRating(@Param("mentorId") Long mentorId);

    int countByMentorId(Long mentorId);

    boolean existsByMentorIdAndReviewerId(Long mentorId, Long reviewerId);
}

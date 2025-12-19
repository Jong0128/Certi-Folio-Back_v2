package com.certifolio.server.repository;

import com.certifolio.server.domain.MentoringSession;
import com.certifolio.server.domain.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

        List<MentoringSession> findByMenteeId(Long menteeId);

        List<MentoringSession> findByMentorId(Long mentorId);

        @Query("SELECT s FROM MentoringSession s WHERE s.mentee.id = :userId OR s.mentor.user.id = :userId ORDER BY s.createdAt DESC")
        List<MentoringSession> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

        List<MentoringSession> findByStatus(SessionStatus status);

        @Query("SELECT s FROM MentoringSession s WHERE s.mentee.id = :menteeId AND s.status IN :statuses")
        List<MentoringSession> findByMenteeIdAndStatusIn(@Param("menteeId") Long menteeId,
                        @Param("statuses") List<SessionStatus> statuses);

        @Query("SELECT COUNT(s) FROM MentoringSession s WHERE s.mentor.id = :mentorId AND s.status = :status")
        int countByMentorIdAndStatus(@Param("mentorId") Long mentorId, @Param("status") SessionStatus status);
}

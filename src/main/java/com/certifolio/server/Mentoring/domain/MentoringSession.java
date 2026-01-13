package com.certifolio.server.Mentoring.domain;

import com.certifolio.server.User.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 멘토링 세션 엔티티
 */
@Entity
@Table(name = "mentoring_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Column(nullable = false)
    private String topic; // 멘토링 주제

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PENDING;

    private LocalDate startDate; // 시작일

    @Builder.Default
    @Column(nullable = false)
    private Integer totalSessions = 1; // 총 세션 수

    @Builder.Default
    @Column(nullable = false)
    private Integer completedSessions = 0; // 완료된 세션 수

    // 다음 세션 정보
    private LocalDate nextSessionDate;
    private LocalTime nextSessionTime;
    private String nextSessionType; // video, offline, chat

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SessionGoal> goals = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method
    public void addGoal(SessionGoal goal) {
        goals.add(goal);
        goal.setSession(this);
    }

    public int getProgress() {
        if (totalSessions == 0)
            return 0;
        return (int) ((completedSessions * 100.0) / totalSessions);
    }
}

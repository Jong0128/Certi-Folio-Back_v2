package com.certifolio.server.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 세션 목표 엔티티
 */
@Entity
@Table(name = "session_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private MentoringSession session;

    @Column(nullable = false)
    private String goal;

    @Builder.Default
    @Column(nullable = false)
    private boolean completed = false;
}

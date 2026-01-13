package com.certifolio.server.Mentoring.dto;

import com.certifolio.server.Mentoring.domain.Mentor;
import com.certifolio.server.Mentoring.domain.MentoringSession;
import com.certifolio.server.Mentoring.domain.SessionGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 멘토링 세션 관련 DTO 모음
 */
public class MentoringSessionDTO {

    /**
     * 세션 목록 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionsResponse {
        private List<SessionItem> sessions;
        private int total;
    }

    /**
     * 세션 아이템
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionItem {
        private Long id;
        private SessionMentor mentor;
        private String status;
        private String topic;
        private String startDate;
        private int totalSessions;
        private int completedSessions;
        private NextSession nextSession;
        private int progress;
        private List<String> goals;

        public static SessionItem from(MentoringSession session) {
            return SessionItem.builder()
                    .id(session.getId())
                    .mentor(SessionMentor.from(session.getMentor()))
                    .status(session.getStatus().name().toLowerCase())
                    .topic(session.getTopic())
                    .startDate(session.getStartDate() != null ? session.getStartDate().toString() : null)
                    .totalSessions(session.getTotalSessions())
                    .completedSessions(session.getCompletedSessions())
                    .nextSession(session.getNextSessionDate() != null ? new NextSession(
                            session.getNextSessionDate().toString(),
                            session.getNextSessionTime() != null ? session.getNextSessionTime().toString() : null,
                            session.getNextSessionType()) : null)
                    .progress(session.getProgress())
                    .goals(session.getGoals().stream()
                            .map(SessionGoal::getGoal)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    /**
     * 세션 내 멘토 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionMentor {
        private String name;
        private String title;
        private String company;
        private List<String> expertise;
        private String profileImage;
        private Double rating;

        public static SessionMentor from(Mentor mentor) {
            return SessionMentor.builder()
                    .name(mentor.getName())
                    .title(mentor.getTitle())
                    .company(mentor.getCompany())
                    .expertise(mentor.getSkills().stream()
                            .map(s -> s.getSkillName())
                            .collect(Collectors.toList()))
                    .profileImage(mentor.getProfileImage())
                    .rating(mentor.getRating())
                    .build();
        }
    }

    /**
     * 다음 세션 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextSession {
        private String date;
        private String time;
        private String type; // video, offline, chat
    }

    /**
     * 세션 생성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSessionRequest {
        private Long mentorId;
        private Long requestId; // 선택적
        private String topic;
        private Integer totalSessions;
        private List<String> goals;
    }

    /**
     * 세션 상태 업데이트 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateSessionStatusRequest {
        private String status; // active, completed, cancelled
    }

    /**
     * 세션 업데이트 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateSessionResponse {
        private boolean success;
        private String message;
    }
}

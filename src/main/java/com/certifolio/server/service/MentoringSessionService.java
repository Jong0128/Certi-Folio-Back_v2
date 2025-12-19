package com.certifolio.server.service;

import com.certifolio.server.domain.*;
import com.certifolio.server.dto.MentoringSessionDTO;
import com.certifolio.server.repository.MentorRepository;
import com.certifolio.server.repository.MentoringSessionRepository;
import com.certifolio.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MentoringSessionService {

        private final MentoringSessionRepository sessionRepository;
        private final MentorRepository mentorRepository;
        private final UserRepository userRepository;

        /**
         * 내 멘토링 세션 목록 조회
         */
        public MentoringSessionDTO.SessionsResponse getMySessions(Long userId) {
                List<MentoringSession> sessions = sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);

                List<MentoringSessionDTO.SessionItem> sessionItems = sessions.stream()
                                .map(MentoringSessionDTO.SessionItem::from)
                                .collect(Collectors.toList());

                return MentoringSessionDTO.SessionsResponse.builder()
                                .sessions(sessionItems)
                                .total(sessionItems.size())
                                .build();
        }

        /**
         * 세션 상세 조회
         */
        public MentoringSessionDTO.SessionItem getSession(Long sessionId) {
                MentoringSession session = sessionRepository.findById(sessionId)
                                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));

                return MentoringSessionDTO.SessionItem.from(session);
        }

        /**
         * 새 세션 생성
         */
        @Transactional
        public MentoringSessionDTO.UpdateSessionResponse createSession(
                        Long userId,
                        MentoringSessionDTO.CreateSessionRequest request) {

                User mentee = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                Mentor mentor = mentorRepository.findById(request.getMentorId())
                                .orElseThrow(() -> new RuntimeException("멘토를 찾을 수 없습니다."));

                MentoringSession session = MentoringSession.builder()
                                .mentor(mentor)
                                .mentee(mentee)
                                .topic(request.getTopic())
                                .status(SessionStatus.PENDING)
                                .startDate(LocalDate.now())
                                .totalSessions(request.getTotalSessions() != null ? request.getTotalSessions() : 1)
                                .completedSessions(0)
                                .goals(new ArrayList<>())
                                .build();

                sessionRepository.save(session);

                // 목표 추가
                if (request.getGoals() != null) {
                        for (String goalText : request.getGoals()) {
                                SessionGoal goal = SessionGoal.builder()
                                                .session(session)
                                                .goal(goalText)
                                                .completed(false)
                                                .build();
                                session.addGoal(goal);
                        }
                }

                sessionRepository.save(session);

                log.info("새 멘토링 세션 생성: menteeId={}, mentorId={}, sessionId={}",
                                userId, request.getMentorId(), session.getId());

                return MentoringSessionDTO.UpdateSessionResponse.builder()
                                .success(true)
                                .message("멘토링 세션이 생성되었습니다.")
                                .build();
        }

        /**
         * 세션 상태 업데이트
         */
        @Transactional
        public MentoringSessionDTO.UpdateSessionResponse updateSessionStatus(
                        Long sessionId,
                        MentoringSessionDTO.UpdateSessionStatusRequest request) {

                MentoringSession session = sessionRepository.findById(sessionId)
                                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));

                try {
                        SessionStatus newStatus = SessionStatus.valueOf(request.getStatus().toUpperCase());
                        session.setStatus(newStatus);

                        // 완료로 변경 시 완료된 세션 수 증가
                        if (newStatus == SessionStatus.COMPLETED) {
                                session.setCompletedSessions(session.getCompletedSessions() + 1);
                        }

                        sessionRepository.save(session);

                        log.info("세션 상태 업데이트: sessionId={}, newStatus={}", sessionId, newStatus);

                        return MentoringSessionDTO.UpdateSessionResponse.builder()
                                        .success(true)
                                        .message("세션 상태가 업데이트되었습니다.")
                                        .build();

                } catch (IllegalArgumentException e) {
                        return MentoringSessionDTO.UpdateSessionResponse.builder()
                                        .success(false)
                                        .message("잘못된 상태값입니다.")
                                        .build();
                }
        }

        /**
         * 멘티의 활성 세션 조회
         */
        public MentoringSessionDTO.SessionsResponse getActiveSessions(Long userId) {
                List<SessionStatus> activeStatuses = List.of(
                                SessionStatus.PENDING,
                                SessionStatus.ACTIVE,
                                SessionStatus.SCHEDULED);

                List<MentoringSession> sessions = sessionRepository.findByMenteeIdAndStatusIn(userId, activeStatuses);

                List<MentoringSessionDTO.SessionItem> sessionItems = sessions.stream()
                                .map(MentoringSessionDTO.SessionItem::from)
                                .collect(Collectors.toList());

                return MentoringSessionDTO.SessionsResponse.builder()
                                .sessions(sessionItems)
                                .total(sessionItems.size())
                                .build();
        }
}

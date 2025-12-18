package com.certifolio.server.controller;

import com.certifolio.server.domain.CareerPreference;
import com.certifolio.server.domain.User;
import com.certifolio.server.dto.CareerPreferenceDTO;
import com.certifolio.server.repository.CareerPreferenceRepository;
import com.certifolio.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final CareerPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    private Long getUserId(Object principal) {
        String subject = principal.toString();
        System.out.println("AnalyticsController.getUserId - Subject: " + subject);
        
        try {
            if (subject.contains(":")) {
                 String[] parts = subject.split(":");
                 System.out.println("Looking up by provider=" + parts[0] + ", providerId=" + parts[1]);
                 return userRepository.findByProviderAndProviderId(parts[0], parts[1])
                     .map(User::getId)
                     .orElseThrow(() -> new RuntimeException("User not found with provider: " + parts[0] + ", id: " + parts[1]));
            } else {
                 System.out.println("Looking up by email=" + subject);
                 return userRepository.findByEmail(subject)
                     .map(User::getId)
                     .orElseThrow(() -> new RuntimeException("User not found with email: " + subject));
            }
        } catch (Exception e) {
            System.out.println("Error in getUserId: " + e.getMessage());
            throw e;
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreferences(@AuthenticationPrincipal Object principal) {
        Long userId = getUserId(principal);
        User user = getUser(userId);
        
        return ResponseEntity.ok(Map.of("preference", preferenceRepository.findByUser(user)
                .map(p -> CareerPreferenceDTO.builder()
                        .jobRole(p.getJobRole())
                        .companyType(p.getCompanyType())
                        .targetCompany(p.getTargetCompany())
                        .updatedAt(p.getUpdatedAt().toString())
                        .build())
                .orElse(null)));
    }

    @PostMapping("/preferences")
    public ResponseEntity<?> savePreferences(@AuthenticationPrincipal Object principal, @RequestBody CareerPreferenceDTO dto) {
        Long userId = getUserId(principal);
        User user = getUser(userId);

        CareerPreference pref = preferenceRepository.findByUser(user).orElse(CareerPreference.builder().user(user).build());
        pref.setJobRole(dto.getJobRole());
        pref.setCompanyType(dto.getCompanyType());
        pref.setTargetCompany(dto.getTargetCompany());
        
        preferenceRepository.save(pref);

        return ResponseEntity.ok(Map.of("success", true, "message", "저장되었습니다."));
    }

    @GetMapping("/skill-analysis")
    public ResponseEntity<?> getSkillAnalysis(@AuthenticationPrincipal Object principal) {
        // TODO: Implement actual skill analysis logic based on user portfolio data
        // For now, return mock target scores that match frontend categories
        return ResponseEntity.ok(Map.of(
            "skills", java.util.List.of(
                Map.of("subject", "자격증", "myScore", 0, "targetScore", 80, "fullMark", 100),
                Map.of("subject", "어학", "myScore", 0, "targetScore", 85, "fullMark", 100),
                Map.of("subject", "경력", "myScore", 0, "targetScore", 75, "fullMark", 100),
                Map.of("subject", "학점", "myScore", 0, "targetScore", 80, "fullMark", 100),
                Map.of("subject", "프로젝트", "myScore", 0, "targetScore", 85, "fullMark", 100),
                Map.of("subject", "대외활동", "myScore", 0, "targetScore", 70, "fullMark", 100)
            ),
            "strengths", java.util.List.of(),
            "weaknesses", java.util.List.of(),
            "recommendations", java.util.List.of(),
            "overallScore", 0
        ));
    }

    @PostMapping("/skill-analysis/refresh")
    public ResponseEntity<?> refreshSkillAnalysis(@AuthenticationPrincipal Object principal) {
        // TODO: Implement actual refresh logic
        return getSkillAnalysis(principal);
    }
}

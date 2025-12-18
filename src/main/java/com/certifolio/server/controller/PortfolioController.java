package com.certifolio.server.controller;

import com.certifolio.server.domain.User;
import com.certifolio.server.dto.*;
import com.certifolio.server.repository.UserRepository;
import com.certifolio.server.service.PortfolioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioServiceImpl portfolioService;
    private final UserRepository userRepository;
    
    // Helper to get User ID
    private Long getUserId(Object principal) {
        // Assuming principal is compatible with how we set it in JwtFilter (String subject)
        // We need to resolve it to ID. Ideally JwtFilter puts UserDetails or ID.
        // Current implementation puts subject (email or provider:id).
        String subject = principal.toString();
        // Since we need ID, we should really look it up or better yet, put ID in token parsing.
        // Let's look up by email or provider/id logic again.
        // This is inefficient, but for now:
        if (subject.contains(":")) {
             String[] parts = subject.split(":");
             return userRepository.findByProviderAndProviderId(parts[0], parts[1]).map(User::getId).orElseThrow();
        } else {
             return userRepository.findByEmail(subject).map(User::getId).orElseThrow();
        }
    }

    @PostMapping("/certificates")
    public ResponseEntity<?> saveCertificates(@AuthenticationPrincipal Object principal, @RequestBody List<CertificateDTO> dtos) {
        portfolioService.saveCertificates(getUserId(principal), dtos);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/certificates")
    public ResponseEntity<?> getCertificates(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", portfolioService.getCertificates(getUserId(principal))));
    }

    @PostMapping("/certificates/add")
    public ResponseEntity<?> addCertificate(@AuthenticationPrincipal Object principal, @RequestBody CertificateDTO dto) {
         CertificateDTO saved = portfolioService.addCertificate(getUserId(principal), dto);
         return ResponseEntity.ok(Map.of("success", true, "certificate", saved));
    }

    @DeleteMapping("/certificates/{id}")
    public ResponseEntity<?> deleteCertificate(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        portfolioService.deleteCertificate(getUserId(principal), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/projects")
    public ResponseEntity<?> saveProjects(@AuthenticationPrincipal Object principal, @RequestBody List<ProjectDTO> dtos) {
        portfolioService.saveProjects(getUserId(principal), dtos);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjects(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", portfolioService.getProjects(getUserId(principal))));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<?> deleteProject(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        portfolioService.deleteProject(getUserId(principal), id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/activities")
    public ResponseEntity<?> saveActivities(@AuthenticationPrincipal Object principal, @RequestBody List<ActivityDTO> dtos) {
        portfolioService.saveActivities(getUserId(principal), dtos);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/activities")
    public ResponseEntity<?> getActivities(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", portfolioService.getActivities(getUserId(principal))));
    }

    @PostMapping("/careers")
    public ResponseEntity<?> saveCareers(@AuthenticationPrincipal Object principal, @RequestBody List<CareerDTO> dtos) {
        portfolioService.saveCareers(getUserId(principal), dtos);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/careers")
    public ResponseEntity<?> getCareers(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", portfolioService.getCareers(getUserId(principal))));
    }
    
    @PostMapping("/educations")
    public ResponseEntity<?> saveEducations(@AuthenticationPrincipal Object principal, @RequestBody List<EducationDTO> dtos) {
        portfolioService.saveEducations(getUserId(principal), dtos);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/educations")
    public ResponseEntity<?> getEducations(@AuthenticationPrincipal Object principal) {
        return ResponseEntity.ok(Map.of("success", true, "data", portfolioService.getEducations(getUserId(principal))));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> saveProfile(@AuthenticationPrincipal Object principal, @RequestBody ProfileUploadDTO dto) {
        Long userId = getUserId(principal);
        
        // Debug logging
        System.out.println("===== PROFILE SAVE REQUEST =====");
        System.out.println("User ID: " + userId);
        System.out.println("HighSchool: " + (dto.getHighSchool() != null ? dto.getHighSchool().getName() : "null"));
        System.out.println("University: " + (dto.getUniversity() != null ? dto.getUniversity().getName() : "null"));
        System.out.println("Projects count: " + (dto.getProjects() != null ? dto.getProjects().size() : 0));
        System.out.println("Activities count: " + (dto.getActivities() != null ? dto.getActivities().size() : 0));
        System.out.println("Certificates count: " + (dto.getCertificates() != null ? dto.getCertificates().size() : 0));
        System.out.println("Experience: " + (dto.getExperience() != null ? 
            "Internships=" + (dto.getExperience().getInternships() != null ? dto.getExperience().getInternships().size() : 0) + 
            ", Jobs=" + (dto.getExperience().getJobs() != null ? dto.getExperience().getJobs().size() : 0) : "null"));
        System.out.println("================================");
        
        portfolioService.saveFullProfile(userId, dto);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllPortfolio(@AuthenticationPrincipal Object principal) {
        Long userId = getUserId(principal);
        Map<String, Object> data = new HashMap<>();
        data.put("certificates", portfolioService.getCertificates(userId));
        data.put("projects", portfolioService.getProjects(userId));
        data.put("activities", portfolioService.getActivities(userId));
        data.put("careers", portfolioService.getCareers(userId));
        data.put("educations", portfolioService.getEducations(userId));
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}

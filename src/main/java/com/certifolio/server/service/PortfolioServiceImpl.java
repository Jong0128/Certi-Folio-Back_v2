package com.certifolio.server.service;

import com.certifolio.server.domain.*;
import com.certifolio.server.dto.*;
import com.certifolio.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioServiceImpl {

    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final ProjectRepository projectRepository;
    private final ActivityRepository activityRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void saveCertificates(Long userId, List<CertificateDTO> dtos) {
        User user = getUser(userId);
        // Simplest strategy: delete all and re-insert for the user to handle updates/deletes cleanly in one go
        // In a real app with many items, we might want diffing. 
        // Given the UI is "Save" whole list, replace all is acceptable for MVP.
        List<Certificate> oldList = certificateRepository.findAllByUser(user);
        certificateRepository.deleteAll(oldList);

        if (dtos == null) return;

        List<Certificate> newList = dtos.stream().map(dto -> Certificate.builder()
                .user(user)
                .name(dto.getName())
                .issuer(dto.getIssuer())
                .issueDate(parseDate(dto.getIssueDate()))
                .expiryDate(parseDate(dto.getExpiryDate()))
                .status(dto.getStatus())
                .score(dto.getScore())
                .certificateNumber(dto.getCertificateNumber())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .build()).collect(Collectors.toList());

        certificateRepository.saveAll(newList);
    }

    public List<CertificateDTO> getCertificates(Long userId) {
        return certificateRepository.findAllByUserId(userId).stream().map(c -> CertificateDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .issuer(c.getIssuer())
                .issueDate(dateToString(c.getIssueDate()))
                .expiryDate(dateToString(c.getExpiryDate()))
                .status(c.getStatus())
                .score(c.getScore())
                .certificateNumber(c.getCertificateNumber())
                .category(c.getCategory())
                .imageUrl(c.getImageUrl())
                .build()).collect(Collectors.toList());
    }
    
    public CertificateDTO addCertificate(Long userId, CertificateDTO dto) {
        User user = getUser(userId);
        Certificate cert = Certificate.builder()
                .user(user)
                .name(dto.getName())
                .issuer(dto.getIssuer())
                .issueDate(parseDate(dto.getIssueDate()))
                .expiryDate(parseDate(dto.getExpiryDate()))
                .status(dto.getStatus())
                .score(dto.getScore())
                .certificateNumber(dto.getCertificateNumber())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .build();
        Certificate saved = certificateRepository.save(cert);
        return CertificateDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .issuer(saved.getIssuer())
                .issueDate(dateToString(saved.getIssueDate()))
                .expiryDate(dateToString(saved.getExpiryDate()))
                .status(saved.getStatus())
                .score(saved.getScore())
                .certificateNumber(saved.getCertificateNumber())
                .category(saved.getCategory())
                .imageUrl(saved.getImageUrl())
                .build();
    }

    public void deleteCertificate(Long userId, Long certificateId) {
        // Validation: check ownership
        Certificate cert = certificateRepository.findById(certificateId).orElseThrow(() -> new RuntimeException("Certificate not found"));
        if (!cert.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        certificateRepository.delete(cert);
    }
    
    public void saveProjects(Long userId, List<ProjectDTO> dtos) {
        User user = getUser(userId);
        projectRepository.findAllByUser(user).forEach(projectRepository::delete);

        if (dtos == null) return;
        List<Project> list = dtos.stream().map(dto -> Project.builder()
                .user(user)
                .name(dto.getName())
                .type(dto.getType())
                .role(dto.getRole())
                .teamSize(dto.getTeamSize())
                .techStack(dto.getTechStack())
                .description(dto.getDescription())
                .githubLink(dto.getGithubLink())
                .demoLink(dto.getDemoLink())
                .result(dto.getResult())
                .startDate(parseDate(dto.getStartDate()))
                .endDate(parseDate(dto.getEndDate()))
                .build()).collect(Collectors.toList());
        projectRepository.saveAll(list);
    }

    public List<ProjectDTO> getProjects(Long userId) {
        return projectRepository.findAllByUser(getUser(userId)).stream().map(p -> ProjectDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .role(p.getRole())
                .teamSize(p.getTeamSize())
                .techStack(p.getTechStack())
                .description(p.getDescription())
                .githubLink(p.getGithubLink())
                .demoLink(p.getDemoLink())
                .result(p.getResult())
                .startDate(dateToString(p.getStartDate()))
                .endDate(dateToString(p.getEndDate()))
                .build()).collect(Collectors.toList());
    }

    // Similar methods for Activity, Career, Education...
    public void deleteProject(Long userId, Long projectId) {
        Project p = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        if (!p.getUser().getId().equals(userId)) {
             throw new RuntimeException("Unauthorized");
        }
        projectRepository.delete(p);
    }

    public void saveActivities(Long userId, List<ActivityDTO> dtos) {
        User user = getUser(userId);
        activityRepository.findAllByUser(user).forEach(activityRepository::delete);
        
        if (dtos == null) return;
        List<Activity> list = dtos.stream().map(dto -> Activity.builder()
                .user(user)
                .name(dto.getName())
                .type(dto.getType())
                .organizer(dto.getOrganizer())
                .role(dto.getRole())
                .period(dto.getPeriod())
                .description(dto.getDescription())
                .link(dto.getLink())
                .result(dto.getResult())
                .build()).collect(Collectors.toList());
        activityRepository.saveAll(list);
    }

    public List<ActivityDTO> getActivities(Long userId) {
        return activityRepository.findAllByUser(getUser(userId)).stream().map(a -> ActivityDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .type(a.getType())
                .organizer(a.getOrganizer())
                .role(a.getRole())
                .period(a.getPeriod())
                .description(a.getDescription())
                .link(a.getLink())
                .result(a.getResult())
                .build()).collect(Collectors.toList());
    }
    
    public void saveCareers(Long userId, List<CareerDTO> dtos) {
        User user = getUser(userId);
        careerRepository.findAllByUser(user).forEach(careerRepository::delete);
        
        if (dtos == null) return;
        List<Career> list = dtos.stream().map(dto -> Career.builder()
                .user(user)
                .company(dto.getCompany())
                .position(dto.getPosition())
                .department(dto.getDepartment())
                .type(dto.getType())
                .startDate(parseDate(dto.getStartDate()))
                .endDate(parseDate(dto.getEndDate()))
                .isCurrent(dto.isCurrent())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .skills(dto.getSkills())
                .build()).collect(Collectors.toList());
        careerRepository.saveAll(list);
    }

    public List<CareerDTO> getCareers(Long userId) {
         return careerRepository.findAllByUser(getUser(userId)).stream().map(c -> CareerDTO.builder()
                .id(c.getId())
                .company(c.getCompany())
                .position(c.getPosition())
                .department(c.getDepartment())
                .type(c.getType())
                .startDate(dateToString(c.getStartDate()))
                .endDate(dateToString(c.getEndDate()))
                .isCurrent(c.isCurrent())
                .location(c.getLocation())
                .description(c.getDescription())
                .skills(c.getSkills())
                .build()).collect(Collectors.toList());
    }
    
    public void saveEducations(Long userId, List<EducationDTO> dtos) {
        User user = getUser(userId);
        educationRepository.findAllByUser(user).forEach(educationRepository::delete);

        if (dtos == null) return;
        List<Education> list = dtos.stream().map(dto -> Education.builder()
                .user(user)
                .type(dto.getType())
                .schoolName(dto.getSchoolName())
                .major(dto.getMajor())
                .degree(dto.getDegree())
                .status(dto.getStatus())
                .startDate(parseDate(dto.getStartDate()))
                .endDate(parseDate(dto.getEndDate()))
                .isCurrent(dto.isCurrent())
                .gpa(dto.getGpa())
                .maxGpa(dto.getMaxGpa())
                .location(dto.getLocation())
                .build()).collect(Collectors.toList());
        educationRepository.saveAll(list);
    }
    
    public List<EducationDTO> getEducations(Long userId) {
         return educationRepository.findAllByUser(getUser(userId)).stream().map(e -> EducationDTO.builder()
                .id(e.getId())
                .type(e.getType())
                .schoolName(e.getSchoolName())
                .major(e.getMajor())
                .degree(e.getDegree())
                .status(e.getStatus())
                .startDate(dateToString(e.getStartDate()))
                .endDate(dateToString(e.getEndDate()))
                .isCurrent(e.isCurrent())
                .gpa(e.getGpa())
                .maxGpa(e.getMaxGpa())
                .location(e.getLocation())
                .build()).collect(Collectors.toList());
    }

    private java.time.LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            // Expecting YYYY-MM
            if (dateStr.matches("\\d{4}-\\d{2}")) {
                return java.time.LocalDate.parse(dateStr + "-01");
            }
            // If full date is provided
            return java.time.LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private String dateToString(java.time.LocalDate date) {
        if (date == null) return null;
        return date.toString();
    }

    public void saveFullProfile(Long userId, ProfileUploadDTO dto) {
        // 1. Educations (HighSchool + University)
        List<EducationDTO> eduList = new java.util.ArrayList<>();
        if (dto.getHighSchool() != null) {
            ProfileUploadDTO.HighSchoolDTO hs = dto.getHighSchool();
            eduList.add(EducationDTO.builder()
                    .type("highschool")
                    .schoolName(hs.getName())
                    .location(hs.getLocation())
                    .startDate(hs.getEntranceDate())
                    .endDate(hs.getGraduationDate())
                    .gpa(Double.valueOf(hs.getGpa() == null || hs.getGpa().isEmpty() ? "0" : hs.getGpa()))
                    .status("graduated") // default
                    .build());
        }
        if (dto.getUniversity() != null) {
            ProfileUploadDTO.UniversityDTO uni = dto.getUniversity();
            eduList.add(EducationDTO.builder()
                    .type("university")
                    .schoolName(uni.getName())
                    .major(uni.getMajor())
                    .degree(uni.getDegree())
                    .startDate(uni.getEntranceDate())
                    .endDate(uni.getGraduationDate())
                    .gpa(Double.valueOf(uni.getGpa() == null || uni.getGpa().isEmpty() ? "0" : uni.getGpa()))
                    .status(uni.getStatus())
                    .build());
        }
        saveEducations(userId, eduList);

        // 2. Projects
        saveProjects(userId, dto.getProjects());

        // 3. Activities
        saveActivities(userId, dto.getActivities());

        // 4. Certificates
        saveCertificates(userId, dto.getCertificates());

        // 5. Careers (Internships + Jobs)
        List<CareerDTO> careerList = new java.util.ArrayList<>();
        if (dto.getExperience() != null) {
             if (dto.getExperience().getInternships() != null) {
                 for (ProfileUploadDTO.InternshipDTO i : dto.getExperience().getInternships()) {
                     careerList.add(CareerDTO.builder()
                             .company(i.getCompany())
                             .position(i.getPosition())
                             .type("intern")
                             .description(i.getDescription())
                             .startDate(i.getPeriod()) 
                             .endDate(i.getPeriod()) 
                             .build());
                 }
             }
             if (dto.getExperience().getJobs() != null) {
                 for (ProfileUploadDTO.JobDTO j : dto.getExperience().getJobs()) {
                     careerList.add(CareerDTO.builder()
                             .company(j.getCompany())
                             .position(j.getPosition())
                             .department(j.getDepartment())
                             .type("job")
                             .description(j.getDescription())
                             .startDate(j.getPeriod()) 
                             .endDate(j.getPeriod()) 
                             .build());
                 }
             }
        }
        saveCareers(userId, careerList);
    }
}

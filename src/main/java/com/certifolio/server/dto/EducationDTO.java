package com.certifolio.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private Long id;
    private String type;
    private String schoolName;
    private String major;
    private String degree;
    private String status;
    private String startDate;  // Changed to String for YYYY-MM format
    private String endDate;    // Changed to String for YYYY-MM format
    private boolean isCurrent;
    private Double gpa;
    private Double maxGpa;
    private String location;
}

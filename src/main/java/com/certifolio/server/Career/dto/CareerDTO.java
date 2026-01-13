package com.certifolio.server.Career.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerDTO {
    private Long id;
    private String company;
    private String position;
    private String department;
    private String type;
    private String startDate;
    private String endDate;
    private boolean isCurrent;
    private String location;
    private String description;
    private String skills;
}

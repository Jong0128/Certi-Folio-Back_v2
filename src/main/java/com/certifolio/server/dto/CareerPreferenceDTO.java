package com.certifolio.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPreferenceDTO {
    private String jobRole;
    private String companyType;
    private String targetCompany;
    private String updatedAt;
}

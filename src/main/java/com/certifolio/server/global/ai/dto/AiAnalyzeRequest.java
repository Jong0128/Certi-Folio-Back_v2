package com.certifolio.server.global.ai.dto;

import java.util.List;

public record AiAnalyzeRequest(
        Long userId,
        String targetRole,
        Object profile,
        List<?> activities,
        List<?> projects,
        List<?> certificates,
        List<?> algorithms,
        List<?> careers,
        List<?> educations
) {
}

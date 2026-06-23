package com.certifolio.server.global.ai.dto;

import java.util.List;

public record AiAnalyzeResponse(
        String summary,
        List<String> strengths,
        List<String> weaknesses,
        List<Recommendation> recommendations,
        String suggestedPortfolioText,
        Double confidence
) {
    public record Recommendation(
            String title,
            String description,
            String priority
    ) {
    }
}

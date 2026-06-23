package com.certifolio.server.global.ai.client;

import com.certifolio.server.global.ai.config.AiApiProperties;
import com.certifolio.server.global.ai.dto.AiAnalyzeRequest;
import com.certifolio.server.global.ai.dto.AiAnalyzeResponse;
import com.certifolio.server.global.apiPayload.code.GeneralErrorCode;
import com.certifolio.server.global.apiPayload.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAnalysisClient {

    private final WebClient webClient;
    private final AiApiProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAnalyzeResponse analyze(AiAnalyzeRequest request) {
        String requestId = UUID.randomUUID().toString();

        try {
            String responseBody = webClient.post()
                    .uri(properties.baseUrl() + "/ai/analyze")
                    .header("X-Request-Id", requestId)
                    .header("X-Internal-Api-Key", properties.internalApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(properties.timeoutSeconds()))
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.warn(
                                "AI analysis API returned error. requestId={}, status={}, body={}",
                                requestId,
                                e.getStatusCode(),
                                e.getResponseBodyAsString()
                        );
                        return Mono.error(new BusinessException(GeneralErrorCode.ANALYTICS_API_ERROR));
                    })
                    .block();

            return objectMapper.readValue(responseBody, AiAnalyzeResponse.class);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("AI analysis API call failed. requestId={}", requestId, e);
            throw new BusinessException(GeneralErrorCode.ANALYTICS_API_ERROR);
        }
    }
}

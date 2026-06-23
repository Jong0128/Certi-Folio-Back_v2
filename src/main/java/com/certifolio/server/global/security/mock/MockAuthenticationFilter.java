package com.certifolio.server.global.security.mock;

import com.certifolio.server.domain.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Profile("local")
public class MockAuthenticationFilter extends OncePerRequestFilter {

    public static final String MOCK_USER_ID_HEADER = "X-Mock-User-Id";
    public static final String MOCK_ROLE_HEADER = "X-Mock-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (hasBearerToken(request) || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String mockUserId = request.getHeader(MOCK_USER_ID_HEADER);
        if (!StringUtils.hasText(mockUserId)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = Long.parseLong(mockUserId);
            String role = resolveRole(request.getHeader(MOCK_ROLE_HEADER));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Local mock authentication applied. userId={}, role={}", userId, role);
        } catch (NumberFormatException e) {
            log.warn("Invalid local mock user id: {}", mockUserId);
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return StringUtils.hasText(authorization) && authorization.startsWith("Bearer ");
    }

    private String resolveRole(String rawRole) {
        if (!StringUtils.hasText(rawRole)) {
            return Role.USER.getKey();
        }
        if (rawRole.startsWith("ROLE_")) {
            return rawRole;
        }
        return "ROLE_" + rawRole;
    }
}

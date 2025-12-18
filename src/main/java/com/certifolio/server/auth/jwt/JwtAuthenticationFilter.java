package com.certifolio.server.auth.jwt;

import com.certifolio.server.domain.User;
import com.certifolio.server.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        
        if (token != null) {
            System.out.println("JwtAuthenticationFilter: Token found for URI: " + request.getRequestURI());
            if (jwtTokenProvider.validateToken(token)) {
                 System.out.println("JwtAuthenticationFilter: Token is valid");
                 String subject = jwtTokenProvider.getSubject(token);
                 Authentication authentication = new UsernamePasswordAuthenticationToken(subject, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                 SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                 System.out.println("JwtAuthenticationFilter: Token validation failed");
            }
        } else {
            // System.out.println("JwtAuthenticationFilter: No token found for URI: " + request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

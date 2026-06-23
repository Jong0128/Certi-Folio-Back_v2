package com.certifolio.server.global.security.mock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class MockUserInitializer {

    private static final long MOCK_USER_ID = 1L;

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public ApplicationRunner ensureLocalMockUser() {
        return args -> {
            Integer count = jdbcTemplate.queryForObject(
                    "select count(*) from users where id = ?",
                    Integer.class,
                    MOCK_USER_ID
            );

            if (count != null && count > 0) {
                return;
            }

            jdbcTemplate.update("""
                    insert into users (id, name, email, picture, role, provider, provider_id, birth_year, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, now(), now())
                    """,
                    MOCK_USER_ID,
                    "Local Mock User",
                    "local-mock@example.com",
                    null,
                    "USER",
                    "mock",
                    "local-user-1",
                    2000
            );

            log.info("Local mock user created. userId={}", MOCK_USER_ID);
        };
    }
}

package com.sparta.slackservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new SecurityAuditorAware();
    }

    public static class SecurityAuditorAware implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.empty();
            }

            try {
                Object principal = auth.getPrincipal();
                if (principal instanceof Long userId) {
                    return Optional.of(userId);
                }
                if (principal instanceof String str && str.matches("\\d+")) {
                    return Optional.of(Long.parseLong(str));
                }
            } catch (Exception e) {
                return Optional.empty();
            }

            return Optional.empty();
        }
    }
}
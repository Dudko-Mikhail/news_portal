package by.dudko.newsportal.config;

import by.dudko.newsportal.dto.user.UserDetailsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class AuditConfiguration {
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.isAuthenticated()) {
                return Optional.empty();
            }
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return Optional.of(userDetails.getId());
        };
    }
}

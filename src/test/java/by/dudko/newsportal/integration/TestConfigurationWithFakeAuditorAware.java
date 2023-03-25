package by.dudko.newsportal.integration;

import by.dudko.newsportal.NewsPortalApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Import(NewsPortalApplication.class)
@TestConfiguration
public class TestConfigurationWithFakeAuditorAware {
    @Bean
    @Primary
    public AuditorAware<Long> testAuditorAware() {
        return () -> Optional.of(1L);
    }
}

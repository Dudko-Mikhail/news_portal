package by.dudko.newsportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/news", "/api/news/{id}", "/api/users/{userId}/news",
                        "/api/comments/{id}", "/api/users/{userId}/comments", "/api/news/{newsId}/comments").permitAll() // guest access rights
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().disable()
                .formLogin().disable();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

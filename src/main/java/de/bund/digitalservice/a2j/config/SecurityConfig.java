package de.bund.digitalservice.a2j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain springSecurityWebFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/sender/submit"))
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers("/.well-known/security.txt")
                    .permitAll()
                    .requestMatchers("/actuator/health/readiness", "/actuator/health/liveness")
                    .permitAll()
                    .requestMatchers("/api/sender/**")
                    .permitAll()
                    .anyRequest()
                    .denyAll())
        .build();
  }
}

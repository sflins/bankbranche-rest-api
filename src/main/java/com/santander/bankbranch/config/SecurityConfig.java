package com.santander.bankbranch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    // Initialize SLF4J logger
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("--- OAuth2 SecurityFilterChain is being configured ---");
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/desafio/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/desafio/distancia", true)
                        .failureUrl("/login?error=true")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque.introspector(opaqueTokenIntrospector()))
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new GitHubOpaqueTokenIntrospector();
    }

    private static class GitHubOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
        private final RestTemplate restTemplate = new RestTemplate();

        @Override
        public OAuth2AuthenticatedPrincipal introspect(String token) {
            // Log the token (for debugging only, avoid in production)
            logger.info("Validating token: {}", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("Accept", "application/json");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Token validation failed with status: {}", response.getStatusCode());
                throw new IllegalArgumentException("Token validation failed: " + response.getStatusCode());
            }

            Map<String, Object> attributes = response.getBody();
            if (attributes == null || !attributes.containsKey("login")) {
                logger.error("Invalid user info response: {}", attributes);
                throw new IllegalArgumentException("Invalid user info response");
            }

            logger.info("Token validated successfully for user: {}", attributes.get("login"));
            return new OAuth2AuthenticatedPrincipal() {
                @Override
                public Map<String, Object> getAttributes() {
                    return attributes;
                }

                @Override
                public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                    return java.util.Collections.emptyList();
                }

                @Override
                public String getName() {
                    return (String) attributes.get("login");
                }
            };
        }
    }
}
package com.santander.bankbranch.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Primary
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new MockOpaqueTokenIntrospector();
    }

    private static class MockOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
        @Override
        public OAuth2AuthenticatedPrincipal introspect(String token) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("login", "test-user");
            return new OAuth2AuthenticatedPrincipal() {
                @Override
                public Map<String, Object> getAttributes() {
                    return attributes;
                }

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return Collections.emptyList();
                }

                @Override
                public String getName() {
                    return "test-user";
                }
            };
        }
    }
}
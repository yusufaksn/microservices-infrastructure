package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoint'ler
                        .pathMatchers("/public/**").permitAll()

                        // Admin rolü kontrolü (Önemli: hasRole("admin") "ROLE_admin" kontrol eder)
                        .pathMatchers("/admin/**").hasRole("admin")

                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()

                        // Diğer tüm istekler authenticated olmalı
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Yazdığımız reactive converter'ı buraya bağlıyoruz
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .build();
    }

    /**
     * Keycloak'un 'realm_access.roles' yapısını parse edip, Spring'in anlayacağı
     * 'ROLE_' önekiyle asenkron akışa (Flux/Mono) çeviren converter.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Flux<GrantedAuthority>>() {
            @Override
            public Flux<GrantedAuthority> convert(Jwt jwt) {
                // Keycloak token yapısı: "realm_access": { "roles": ["admin", "user"] }
                Map<String, Object> realmAccess = jwt.getClaim("realm_access");

                if (realmAccess == null || !realmAccess.containsKey("roles")) {
                    return Flux.empty();
                }

                Collection<String> roles = (Collection<String>) realmAccess.get("roles");

                // Rollerin başına "ROLE_" ekleyerek SimpleGrantedAuthority nesnesine
                // çeviriyoruz
                java.util.List<GrantedAuthority> authorities = roles.stream()
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                        .collect(Collectors.toList());

                return Flux.fromIterable(authorities);
            }
        });

        return jwtAuthenticationConverter;
    }
}
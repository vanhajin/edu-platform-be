package org.store.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@ConfigurationProperties(prefix = "security")
public class GatewaySecurityConfiguration {

    private List<String> publicPostPaths = List.of();

    public void setPublicPostPaths(List<String> publicPostPaths) {
        this.publicPostPaths = publicPostPaths;
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtTokenService jwtTokenService
    ) {
        return http
                .addFilterAt(
                        new GatewayJwtWebFilter(jwtTokenService),
                        SecurityWebFiltersOrder.AUTHENTICATION
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, exception) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().remove("WWW-Authenticate");
                            return exchange.getResponse().setComplete();
                        }))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, publicPostPaths.toArray(String[]::new)).permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}

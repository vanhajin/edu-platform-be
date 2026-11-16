package org.store.apigateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

public class GatewayJwtWebFilter implements WebFilter {

    private final JwtTokenService jwtTokenService;

    public GatewayJwtWebFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("GATEWAY: "
                + exchange.getRequest().getMethod()
                + " "
                + exchange.getRequest().getURI());
        String token = resolveBearerToken(exchange);

        if (token == null) {
            return chain.filter(exchange);
        }

        try {
            Claims claims = jwtTokenService.verifyAccessToken(token);
            String userId = claims.getSubject();
            String sessionId = claims.get("session_id", String.class);
            String role = claims.get("role", String.class);

            List<SimpleGrantedAuthority> authorities = role == null
                    ? List.of()
                    : List.of(new SimpleGrantedAuthority(role));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );

            ServerWebExchange authenticatedExchange = exchange.mutate()
                    .request(request -> request.headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");
                        headers.remove("X-Session-Id");
                        headers.set("X-User-Id", userId);
                        if (role != null) {
                            headers.set("X-User-Role", role);
                        }
                        if (sessionId != null) {
                            headers.set("X-Session-Id", sessionId);
                        }
                    }))
                    .build();

            return chain.filter(authenticatedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (RuntimeException exception) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String resolveBearerToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}

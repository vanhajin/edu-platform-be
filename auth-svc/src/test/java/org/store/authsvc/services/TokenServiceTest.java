package org.store.authsvc.services;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private final TokenService tokenService = new TokenService(
            Base64.getEncoder().encodeToString("a-secure-test-key-with-at-least-thirty-two-bytes".getBytes()),
            900,
            30
    );

    @Test
    void hashesRefreshTokensWithoutReturningTheOriginalToken() {
        String refreshToken = tokenService.generateRefreshToken();

        assertThat(tokenService.hashRefreshToken(refreshToken))
                .isNotEqualTo(refreshToken)
                .hasSize(64)
                .isEqualTo(tokenService.hashRefreshToken(refreshToken));
    }

    @Test
    void createsAccessTokenWithConfiguredExpiration() {
        String accessToken = tokenService.generateAccessToken("user-id", "student", "STUDENT");

        assertThat(accessToken).contains(".");
        assertThat(tokenService.accessTokenExpirationSeconds()).isEqualTo(900);
    }
}

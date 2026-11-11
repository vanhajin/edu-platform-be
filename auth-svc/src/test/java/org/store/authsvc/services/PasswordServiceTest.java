package org.store.authsvc.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService();

    @Test
    void encodesPasswordWithBcryptAndMatchesTheOriginalPassword() {
        String rawPassword = "A123456";

        String encodedPassword = passwordService.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword).startsWith("$2");
        assertThat(passwordService.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void rejectsAnIncorrectPassword() {
        String encodedPassword = passwordService.encode("A123456");

        assertThat(passwordService.matches("wrong-password", encodedPassword)).isFalse();
    }
}

package org.store.authsvc.dtos.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.store.authsvc.dtos.request.authentication.LoginRequestDTO;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsAValidLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO("VanNT_Buyer", "A123456");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankAccountAndPassword() {
        LoginRequestDTO request = new LoginRequestDTO("", "");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("account", "password");
    }

    @Test
    void rejectsPasswordLongerThan50Characters() {
        LoginRequestDTO request = new LoginRequestDTO("VanNT_Buyer", "a".repeat(51));

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("password");
    }
}

package org.store.authsvc.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.store.authsvc.utils.validation.AccountValidator;
import org.store.authsvc.utils.validation.ValidAccount;

import static org.assertj.core.api.Assertions.assertThat;

class AccountValidatorTest {

    @Test
    void acceptsValidEmailWithAtMostOneHundredCharacters() {
        assertThat(AccountValidator.isValid("Student_01@example.com")).isTrue();
        assertThat(AccountValidator.isValid("a".repeat(88) + "@example.com")).isTrue();
    }

    @Test
    void rejectsInvalidOrTooLongEmail() {
        assertThat(AccountValidator.isValid("student@example")).isFalse();
        assertThat(AccountValidator.isValid("a".repeat(89) + "@example.com")).isFalse();
    }

    @Test
    void acceptsCaseInsensitiveUsernameWithLettersNumbersAndUnderscores() {
        assertThat(AccountValidator.isValid("VanNT_Buyer01")).isTrue();
        assertThat(AccountValidator.isValid("a".repeat(50))).isTrue();
    }

    @Test
    void rejectsUsernameWithOtherCharactersOrMoreThanFiftyCharacters() {
        assertThat(AccountValidator.isValid("VanNT-Buyer")).isFalse();
        assertThat(AccountValidator.isValid("a".repeat(51))).isFalse();
        assertThat(AccountValidator.isValid(null)).isFalse();
        assertThat(AccountValidator.isValid(" ")).isFalse();
    }

    @Test
    void exposesAccountRuleAsBeanValidationAnnotation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertThat(validator.validate(new LoginRequest("Student_01@example.com"))).isEmpty();
        assertThat(validator.validate(new LoginRequest("student-name")))
                .extracting(violation -> violation.getMessage())
                .containsExactly("Tài khoản không đúng định dạng");
    }

    @Test
    void returnsSeparateMessageWhenEmailExceedsMaximumLength() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertThat(validator.validate(new LoginRequest("a".repeat(89) + "@example.com")))
                .extracting(violation -> violation.getMessage())
                .containsExactly("Tài khoản không được vượt quá 100 ký tự");
    }

    @Test
    void returnsSeparateMessageWhenUsernameExceedsMaximumLength() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertThat(validator.validate(new LoginRequest("a".repeat(51))))
                .extracting(violation -> violation.getMessage())
                .containsExactly("Tài khoản không được vượt quá 50 ký tự");
    }

    private record LoginRequest(@ValidAccount(fieldName = "Tài khoản") String account) {
    }
}

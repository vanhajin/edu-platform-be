package org.store.authsvc.dtos.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistorRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsAValidRegistrationRequest() {
        RegistorRequestDTO request = new RegistorRequestDTO(
                "VanNT_Buyer",
                "nguyenthivanhs98@gmail.com",
                "A123456",
                "A123456",
                "Nguyễn Thị Vân",
                "STUDENT",
                "1998",
                "Hải Dương"
        );

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankFieldsAndInvalidFormats() {
        RegistorRequestDTO request = new RegistorRequestDTO(
                "Van NT!",
                "not-an-email",
                "",
                "",
                "",
                "",
                "199a",
                ""
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("username", "email", "password", "displayName", "role", "yearOfBirth");
    }
}

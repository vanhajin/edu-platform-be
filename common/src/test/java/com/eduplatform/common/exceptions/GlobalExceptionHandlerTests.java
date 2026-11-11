package com.eduplatform.common.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsBadRequestForValidationErrors() {
        BindException exception = new BindException(new Object(), "request");
        exception.addError(new FieldError("request", "email", "Email is invalid"));

        var response = handler.handleValidation(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Email is invalid");
        assertThat(response.getBody().getOtherMessage()).contains("default message [Email is invalid]");
    }

    @Test
    void returnsInternalServerErrorWithLogicCodeAndMessage() {
        var response = handler.handleLogic(new BaseException(
                "USERNAME_EXISTS", "Username already exists"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getCode()).isEqualTo("USERNAME_EXISTS");
        assertThat(response.getBody().getMessage()).isEqualTo("Username already exists");
        assertThat(response.getBody().getOtherMessage()).contains(":Username already exists");
    }

    @Test
    void returnsBadRequestForMalformedJson() {
        var response = handler.handleMalformedInput(new HttpMessageNotReadableException(
                "Malformed JSON", new MockHttpInputMessage(new byte[0])));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON");
    }
}

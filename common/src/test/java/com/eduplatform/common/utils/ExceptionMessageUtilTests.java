package com.eduplatform.common.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionMessageUtilTests {

    @Test
    void formatsRootExceptionClassMethodLineAndMessage() {
        String detail = ExceptionMessageUtil.getDetail("auth-svc", captureException());

        assertThat(detail).startsWith("auth-svc:");
        assertThat(detail).contains("ExceptionMessageUtilTests:captureException:");
        assertThat(detail).endsWith(":Database connection failed");
    }

    private RuntimeException captureException() {
        return new IllegalStateException("Database connection failed");
    }
}

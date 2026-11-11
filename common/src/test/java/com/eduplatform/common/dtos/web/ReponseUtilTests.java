package com.eduplatform.common.dtos.web;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReponseUtilTests {

    @Test
    void createsSuccessResponseWithPayload() {
        Map<String, String> data = Map.of("userId", "u-01");

        ReponseUtil<Map<String, String>> response = ReponseUtil.success(data);

        assertThat(response.getCode()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getData()).isEqualTo(data);
    }

    @Test
    void createsResponseWithProvidedCodeMessageAndPayload() {
        ReponseUtil<String> response = ReponseUtil.of("USER_CREATED", "User created", "u-01");

        assertThat(response.getCode()).isEqualTo("USER_CREATED");
        assertThat(response.getMessage()).isEqualTo("User created");
        assertThat(response.getData()).isEqualTo("u-01");
    }

    @Test
    void createsResponseWithTechnicalOtherMessage() {
        ReponseUtil<Void> response = ReponseUtil.error("INTERNAL_ERROR", "Request failed", "Service.method:10:Database unavailable");

        assertThat(response.getCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getMessage()).isEqualTo("Request failed");
        assertThat(response.getOtherMessage()).isEqualTo("Service.method:10:Database unavailable");
    }
}

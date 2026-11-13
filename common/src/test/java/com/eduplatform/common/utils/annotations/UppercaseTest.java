package com.eduplatform.common.utils.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UppercaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void trimsAndUppercasesAnnotatedText() throws Exception {
        SampleRequest request = objectMapper.readValue("{\"code\":\"  ab-cd  \"}", SampleRequest.class);

        assertThat(request.code).isEqualTo("AB-CD");
    }

    @Test
    void keepsNullAndEmptyTextUnchanged() throws Exception {
        SampleRequest nullRequest = objectMapper.readValue("{\"code\":null}", SampleRequest.class);
        SampleRequest emptyRequest = objectMapper.readValue("{\"code\":\"\"}", SampleRequest.class);

        assertThat(nullRequest.code).isNull();
        assertThat(emptyRequest.code).isEmpty();
    }

    static class SampleRequest {

        @Uppercase
        public String code;
    }
}

package com.eduplatform.common.utils.annotations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.util.Locale;

public final class UppercaseDeserializer extends StdScalarDeserializer<String> {

    public UppercaseDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}

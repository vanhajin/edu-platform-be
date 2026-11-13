package com.eduplatform.common.utils.annotations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Normalizes an incoming JSON string by trimming and converting it to uppercase. */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JsonDeserialize(using = UppercaseDeserializer.class)
public @interface Uppercase {
}

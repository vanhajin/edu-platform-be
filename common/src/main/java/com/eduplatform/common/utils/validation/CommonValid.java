package com.eduplatform.common.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CommonValidValidator.class)
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface CommonValid {

    String fieldName();

    boolean notNull() default false;

    String regex() default "";

    int min() default -1;

    int max() default -1;

    String dateFormat() default "";

    String message() default "Giá trị không hợp lệ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

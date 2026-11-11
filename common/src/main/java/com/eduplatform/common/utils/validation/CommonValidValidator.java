package com.eduplatform.common.utils.validation;

import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class CommonValidValidator implements ConstraintValidator<CommonValid, String> {

    private String fieldName;
    private boolean notNull;
    private Pattern regexPattern;
    private int min;
    private int max;
    private String dateFormat;
    private DateTimeFormatter dateFormatter;

    @Override
    public void initialize(CommonValid constraintAnnotation) {
        fieldName = constraintAnnotation.fieldName();
        notNull = constraintAnnotation.notNull();
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
        dateFormat = constraintAnnotation.dateFormat();

        if (fieldName == null || fieldName.isBlank()) {
            throw new ConstraintDeclarationException("fieldName không được để trống");
        }
        if (min < -1 || max < -1 || (min >= 0 && max >= 0 && min > max)) {
            throw new ConstraintDeclarationException("min và max không hợp lệ");
        }

        if (!constraintAnnotation.regex().isBlank()) {
            try {
                regexPattern = Pattern.compile(constraintAnnotation.regex());
            } catch (PatternSyntaxException exception) {
                throw new ConstraintDeclarationException("regex không hợp lệ", exception);
            }
        }

        if (!dateFormat.isBlank()) {
            try {
                dateFormatter = DateTimeFormatter.ofPattern(toProlepticYearPattern(dateFormat))
                        .withResolverStyle(ResolverStyle.STRICT);
            } catch (IllegalArgumentException exception) {
                throw new ConstraintDeclarationException("dateFormat không hợp lệ", exception);
            }
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return !notNull || addViolation(context, "không được để trống");
        }
        if (regexPattern != null && !regexPattern.matcher(value).matches()) {
            return addViolation(context, "không đúng định dạng");
        }
        if (min >= 0 && max >= 0 && (value.length() < min || value.length() > max)) {
            return addViolation(context, "độ dài phải từ " + min + " đến " + max);
        }
        if (min >= 0 && value.length() < min) {
            return addViolation(context, "độ dài tối thiểu là " + min);
        }
        if (max >= 0 && value.length() > max) {
            return addViolation(context, "độ dài tối đa là " + max);
        }
        if (dateFormatter != null) {
            try {
                LocalDate.parse(value, dateFormatter);
            } catch (DateTimeParseException exception) {
                return addViolation(context, "không đúng định dạng ngày " + dateFormat);
            }
        }
        return true;
    }

    private boolean addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(fieldName + " " + message)
                .addConstraintViolation();
        return false;
    }

    private String toProlepticYearPattern(String pattern) {
        return pattern.replace('y', 'u');
    }
}

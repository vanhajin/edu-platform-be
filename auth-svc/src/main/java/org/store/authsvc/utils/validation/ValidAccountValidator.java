package org.store.authsvc.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public final class ValidAccountValidator implements ConstraintValidator<ValidAccount, String> {

    private String fieldName;
    private String message;

    @Override
    public void initialize(ValidAccount constraintAnnotation) {
        fieldName = constraintAnnotation.fieldName();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String account, ConstraintValidatorContext context) {
        AccountValidator.ValidationError validationError = AccountValidator.validate(account);
        if (validationError == AccountValidator.ValidationError.NONE) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(fieldName + " " + toMessage(validationError))
                .addConstraintViolation();
        return false;
    }

    private String toMessage(AccountValidator.ValidationError validationError) {
        return switch (validationError) {
            case EMAIL_TOO_LONG -> "không được vượt quá 100 ký tự";
            case USERNAME_TOO_LONG -> "không được vượt quá 50 ký tự";
            case INVALID_FORMAT -> message;
            case NONE -> throw new IllegalStateException("Không có lỗi validation");
        };
    }
}

package org.store.authsvc.utils.validation;

import java.util.regex.Pattern;

public final class AccountValidator {

    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@"
                    + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");

    private AccountValidator() {
    }

    public static boolean isValid(String account) {
        return validate(account) == ValidationError.NONE;
    }

    static ValidationError validate(String account) {
        if (account == null || account.isBlank()) {
            return ValidationError.INVALID_FORMAT;
        }
        if (account.contains("@")) {
            if (account.length() > MAX_EMAIL_LENGTH) {
                return ValidationError.EMAIL_TOO_LONG;
            }
            return EMAIL_PATTERN.matcher(account).matches()
                    ? ValidationError.NONE
                    : ValidationError.INVALID_FORMAT;
        }
        if (account.length() > MAX_USERNAME_LENGTH) {
            return ValidationError.USERNAME_TOO_LONG;
        }
        return USERNAME_PATTERN.matcher(account).matches()
                ? ValidationError.NONE
                : ValidationError.INVALID_FORMAT;
    }

    enum ValidationError {
        NONE,
        INVALID_FORMAT,
        EMAIL_TOO_LONG,
        USERNAME_TOO_LONG
    }
}

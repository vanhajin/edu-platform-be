package com.eduplatform.common.utils.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommonValidValidatorTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Test
    void acceptsNullWhenNotNullIsDisabled() {
        assertThat(validate(new OptionalValue(null))).isEmpty();
    }

    @Test
    void rejectsNullWithVietnameseFieldMessageWhenRequired() {
        assertThat(messages(validate(new RequiredValue(null))))
                .containsExactly("Tên đăng nhập không được để trống");
    }

    @Test
    void rejectsStringThatDoesNotFullyMatchRegex() {
        assertThat(messages(validate(new RegexValue("AB12"))))
                .containsExactly("Mã lớp không đúng định dạng");
    }

    @Test
    void rejectsStringShorterThanConfiguredMinimumLength() {
        assertThat(messages(validate(new MinimumLengthValue("abc"))))
                .containsExactly("Mật khẩu độ dài tối thiểu là 4");
    }

    @Test
    void rejectsStringLongerThanConfiguredMaximumLength() {
        assertThat(messages(validate(new MaximumLengthValue("abcdef"))))
                .containsExactly("Biệt danh độ dài tối đa là 5");
    }

    @Test
    void acceptsStringInsideInclusiveLengthRange() {
        assertThat(validate(new RangeLengthValue("abcd"))).isEmpty();
    }

    @Test
    void rejectsStringOutsideInclusiveLengthRange() {
        assertThat(messages(validate(new RangeLengthValue("a"))))
                .containsExactly("Mã sinh viên độ dài phải từ 2 đến 5");
    }

    @Test
    void validatesDateUsingConfiguredFormatStrictly() {
        assertThat(validate(new DateValue("29/02/2024"))).isEmpty();
        assertThat(messages(validate(new DateValue("31/02/2026"))))
                .containsExactly("Ngày sinh không đúng định dạng ngày dd/MM/uuuu");
    }

    private static Set<?> validate(Object value) {
        return validator.validate(value);
    }

    private static Set<String> messages(Set<?> violations) {
        return violations.stream()
                .map(violation -> ((jakarta.validation.ConstraintViolation<?>) violation).getMessage())
                .collect(java.util.stream.Collectors.toSet());
    }

    private static final class OptionalValue {
        @CommonValid(fieldName = "Ghi chú", regex = "[A-Z]+")
        private final String value;

        private OptionalValue(String value) {
            this.value = value;
        }
    }

    private static final class RequiredValue {
        @CommonValid(fieldName = "Tên đăng nhập", notNull = true)
        private final String value;

        private RequiredValue(String value) {
            this.value = value;
        }
    }

    private static final class RegexValue {
        @CommonValid(fieldName = "Mã lớp", regex = "[A-Z]+")
        private final String value;

        private RegexValue(String value) {
            this.value = value;
        }
    }

    private static final class MinimumLengthValue {
        @CommonValid(fieldName = "Mật khẩu", min = 4)
        private final String value;

        private MinimumLengthValue(String value) {
            this.value = value;
        }
    }

    private static final class MaximumLengthValue {
        @CommonValid(fieldName = "Biệt danh", max = 5)
        private final String value;

        private MaximumLengthValue(String value) {
            this.value = value;
        }
    }

    private static final class RangeLengthValue {
        @CommonValid(fieldName = "Mã sinh viên", min = 2, max = 5)
        private final String value;

        private RangeLengthValue(String value) {
            this.value = value;
        }
    }

    private static final class DateValue {
        @CommonValid(fieldName = "Ngày sinh", dateFormat = "dd/MM/uuuu")
        private final String value;

        private DateValue(String value) {
            this.value = value;
        }
    }
}

# Common validation design

## Intent

Provide one reusable Bean Validation annotation for DTO string fields. It lets each field opt into null, regex, length, and date-format checks without duplicating validation code.

## Goals

- Add `@CommonValid` in the common module for use with Spring `@Valid`.
- Configuration: `fieldName`, `notNull` (default `false`), `regex`, `min`, `max`, and `dateFormat`.
- Only apply optional rules when their configuration is supplied.
- Treat `min` and `max` as `String` length limits; when both are supplied, require an inclusive range.
- Return a Vietnamese, field-specific validation message.

## Non-goals

- Numeric bounds, collection sizes, date ranges, or cross-field validation.
- Replacing standard Jakarta validation annotations.

## Approaches considered

1. Custom Jakarta Bean Validation annotation and `ConstraintValidator` (chosen): fields declare their rules; Spring collects violations through existing `@Valid` handling.
2. Static utility called manually: requires each caller to invoke it and map errors.
3. Reflection-based object validator: more opaque and harder to integrate with standard validation responses.

## Behavior

- `null` is invalid only when `notNull = true`. If `notNull = false`, a null value bypasses every other rule.
- For non-null strings: a configured `regex` must match the whole string; configured `min` and `max` validate length independently or inclusively together; configured `dateFormat` must parse the complete text using that formatter.
- The first failed rule determines the message, prefixed by `fieldName`.
- Default messages: `không được để trống`, `không đúng định dạng`, `độ dài tối thiểu là {min}`, `độ dài tối đa là {max}`, `độ dài phải từ {min} đến {max}`, and `không đúng định dạng ngày {dateFormat}`.

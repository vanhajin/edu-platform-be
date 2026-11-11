# Common Validation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a reusable field-level Jakarta Bean Validation annotation for optional string validation rules with Vietnamese messages.

**Architecture:** A `@CommonValid` annotation declares a field label and optional rules. `CommonValidValidator` implements Jakarta's `ConstraintValidator`, treats null values according to `notNull`, and reports the first failed rule through the standard Bean Validation context.

**Tech Stack:** Java, Jakarta Validation API 3.1, JUnit Jupiter, AssertJ.

**Spec:** `docs/superpowers/specs/2026-11-11-common-valid-design.md`

## Global Constraints

- Validate strings only; `min` and `max` mean inclusive string length.
- `notNull` defaults to `false`; null otherwise bypasses optional rules.
- A configured regex must match the entire string.
- A configured date format validates complete parsing of the string.
- Every default violation must be prefixed by the supplied Vietnamese `fieldName`.

## Review Focus

- Empty string with `notNull = true` must remain valid unless another configured rule rejects it.
- A malformed regex configuration should produce a clear validation-time failure, not silently pass input.
- Invalid date values such as 31/02/2026 must be rejected using strict parsing.
- When only one bound is configured, it must be independently enforced.
- Configuring `min > max` must fail predictably rather than make every input ambiguous.

---

### Task 1: Common validation annotation and validator

**Files:**
- Create: `common/src/main/java/com/eduplatform/common/utils/validation/CommonValid.java`
- Create: `common/src/main/java/com/eduplatform/common/utils/validation/CommonValidValidator.java`
- Test: `common/src/test/java/com/eduplatform/common/utils/validation/CommonValidValidatorTests.java`

**Interfaces:**
- Produces: `@CommonValid` with `String fieldName()`, `boolean notNull() default false`, `String regex() default ""`, `int min() default -1`, `int max() default -1`, and `String dateFormat() default ""`.
- Produces: `CommonValidValidator implements ConstraintValidator<CommonValid, String>`.

- [ ] **Step 1: Write failing validator tests**

Cover null required/optional behavior, regex full matching, min-only, max-only, inclusive range, strict valid/invalid dates, and each Vietnamese message prefixed with `fieldName`.

- [ ] **Step 2: Run the focused test to verify it fails**

Run: `./mvnw -pl common -Dtest=CommonValidValidatorTests test`
Expected: FAIL because the annotation and validator do not exist.

- [ ] **Step 3: Implement `CommonValid` and `CommonValidValidator`**

Use `@Constraint(validatedBy = CommonValidValidator.class)`, disable the default constraint violation when a rule fails, and create the rule-specific Vietnamese violation message. Compile regexes once during initialization; use strict `DateTimeFormatter` parsing for configured dates; reject invalid configuration where `min > max`.

- [ ] **Step 4: Run the focused test to verify it passes**

Run: `./mvnw -pl common -Dtest=CommonValidValidatorTests test`
Expected: PASS.

- [ ] **Step 5: Run the common-module suite**

Run: `./mvnw -pl common test`
Expected: PASS with no regressions.

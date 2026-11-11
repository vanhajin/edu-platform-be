# EduPlatform BE

Java 21 / Maven microservice foundation using Spring Boot 4.1.2 and Spring Cloud 2025.1.2.

## Modules

- `parent`: Maven reactor for building all modules.
- `common`: shared `eduplatform.datasource` auto-configuration.
- `api-gateway`: gateway at port `6868`, routing `/auth/**` to auth service.
- `auth-svc`: MySQL/JPA service at port `45900`.

## Build

From `parent`, run `mvn clean package`.

## Environment profiles

Set `SPRING_PROFILES_ACTIVE=dev` for local development or `SPRING_PROFILES_ACTIVE=prod` in production.

- Gateway `dev` routes `/auth/**` to `http://localhost:45900`.
- Gateway `prod` routes `/auth/**` to `AUTH_SVC_URL` (default: `http://auth-svc:45900`).
- Auth `dev` uses local MySQL defaults and schema mode `update`.
- Auth `prod` requires all three database environment variables and uses schema mode `validate`.

## Database configuration

Before running `auth-svc`, set `EDUPLATFORM_DB_URL`, `EDUPLATFORM_DB_USERNAME`, and `EDUPLATFORM_DB_PASSWORD`. MySQL is required at runtime but not for Maven build/tests.

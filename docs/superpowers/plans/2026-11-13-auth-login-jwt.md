# JWT Login Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Authenticate a user by username or email, return a signed JWT access token, and persist a hashed refresh-token session.

**Architecture:** `AuthService.login` loads an internal login projection that contains the BCrypt password hash and user information. The public response projection remains separate so a password hash can never appear in `LoginResponseDTO`. A dedicated token service signs short-lived access JWTs using a secret injected from environment configuration, while an independently generated refresh token is SHA-256 hashed and stored through `UserSessionsRepository`.

**Tech Stack:** Spring Boot, Spring Data JPA, Spring Security BCrypt, MapStruct, JJWT.

**Spec:** User-provided login-flow requirements in this chat.

## Global Constraints

- `JWT_SECRET` is provided through environment configuration and never committed as a real secret.
- Access tokens expire in 900 seconds; refresh sessions expire after 30 days.
- Password validation uses `PasswordService.matches`, never a direct hash comparison.
- Only refresh-token hashes are stored in `user_sessions`.

## Review Focus

- Unknown account must produce `LOGIN_ACCOUNT_PASSWORD_FAIL` without saving a session.
- Wrong password must produce the same error without leaking account existence.
- Inactive users (`status != 1`) must not authenticate.
- Refresh token persisted to the database must not match the raw token returned to the client.
- Invalid or missing `JWT_SECRET` must stop application startup rather than issue unsigned tokens.

---

### Task 1: Add internal login projection and account lookup

**Files:**
- Modify: `auth-svc/src/main/java/org/store/authsvc/repositories/UserRepository.java`
- Create: `auth-svc/src/main/java/org/store/authsvc/dtos/persistence/IUserLoginDTO.java`
- Test: `auth-svc/src/test/java/org/store/authsvc/repositories/UserRepositoryTest.java`

**Interfaces:**
- Produces: `Optional<IUserLoginDTO> findLoginByUsernameOrEmail(String account)`. `IUserLoginDTO` contains `password` for BCrypt comparison and the response fields; `IUserInfoDTO` remains public-response-only.

- [ ] Write failing repository contract tests for active account lookup and the internal login projection.
- [ ] Implement a derived-query lookup wrapped by a one-argument default method. `IUserLoginDTO` includes `getPassword()` for bcrypt verification and is never included in the API response.
- [ ] Run repository tests and confirm they pass.

### Task 2: Add signed-token service

**Files:**
- Create: `auth-svc/src/main/java/org/store/authsvc/services/TokenService.java`
- Modify: `auth-svc/src/main/resources/application.yaml`
- Modify: `auth-svc/pom.xml`
- Test: `auth-svc/src/test/java/org/store/authsvc/services/TokenServiceTest.java`

**Interfaces:**
- Produces: `String generateAccessToken(UsersEntity user)`, `String generateRefreshToken()`, and `String hashRefreshToken(String token)`.

- [ ] Write failing tests for token expiration, token subject, refresh-token entropy, and stable SHA-256 hashing.
- [ ] Add JJWT dependencies and environment-backed JWT configuration.
- [ ] Implement the token service without exposing the signing secret.
- [ ] Run token-service tests and confirm they pass.

### Task 3: Implement login and persist session

**Files:**
- Modify: `auth-svc/src/main/java/org/store/authsvc/services/AuthService.java`
- Modify: `auth-svc/src/main/java/org/store/authsvc/mappers/UserSessionsMap.java`
- Test: `auth-svc/src/test/java/org/store/authsvc/services/AuthServiceTest.java`

**Interfaces:**
- Consumes: account lookup from Task 1 and token-service methods from Task 2.
- Produces: `LoginResponseDTO login(LoginRequestDTO request)`.

- [ ] Write failing tests for successful login, unknown account, invalid password, and inactive account.
- [ ] Implement `login`: look up the user, verify BCrypt password, generate tokens, hash only the refresh token, map/save the session, and build the response.
- [ ] Run service tests and confirm they pass.

### Task 4: Expose the login endpoint

**Files:**
- Modify: `auth-svc/src/main/java/org/store/authsvc/controllers/AuthController.java`
- Test: `auth-svc/src/test/java/org/store/authsvc/controllers/AuthControllerTest.java`

**Interfaces:**
- Consumes: `AuthService.login(LoginRequestDTO request)`.
- Produces: `POST /auth/login` returning `LoginResponseDTO`.

- [ ] Write a failing controller test for `POST /auth/login`.
- [ ] Add the controller endpoint and return the standard success wrapper.
- [ ] Run controller tests and the module test suite.

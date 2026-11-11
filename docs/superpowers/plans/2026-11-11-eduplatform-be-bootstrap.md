# EduPlatform BE Bootstrap Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a buildable Java 21 Maven microservice skeleton with a shared database configuration module, API gateway, and auth service.

**Architecture:** `parent` is the Maven reactor aggregator and inherits Spring Boot's parent POM. `common` is a non-executable library whose auto-configuration supplies the shared `eduplatform.datasource` to Spring Boot datasource properties. `auth-svc` is the MySQL/JPA service, and the reactive `api-gateway` routes `/auth/**` to it.

**Tech Stack:** Java 21, Maven Wrapper, Spring Boot 4.1.2, Spring Cloud 2025.1.2, Spring Cloud Gateway WebFlux, Spring Data JPA, MySQL Connector/J, Lombok, YAML.

**Spec:** `docs/superpowers/specs/2026-11-11-eduplatform-be-design.md`

## Global Constraints

- Use Java `21`, Maven and YAML; build executable services as JAR files.
- Use Spring Boot `4.1.2` and Spring Cloud BOM `2025.1.2`.
- `parent` must build the complete reactor from its own directory.
- `api-gateway` listens on `6868`; `auth-svc` listens on `45900`.
- Database credentials must be overridden through environment variables, never committed.
- A build must not require a running MySQL instance; launching `auth-svc` does.

## Review Focus

- A production environment variable password containing special characters must bind unchanged; pin this in the `common` properties binding test.
- Starting `auth-svc` without a reachable MySQL must fail rather than silently use an in-memory database; pin with a configuration-only test/no H2 dependency.
- Gateway must remain free of JPA and the MySQL driver; pin with Maven dependency-tree verification.
- A request to `/auth/**` must keep its path when proxied to `http://localhost:45900`; pin with a gateway route-unit test.
- `parent/mvnw clean package` must yield JARs for all three modules; pin with reactor integration verification.

---

## File Structure

- `parent/pom.xml` — reactor modules, shared coordinates/properties, imported Cloud BOM and plugin management.
- `common/pom.xml` — plain shared library dependencies and Spring Boot auto-configuration processor.
- `common/src/main/java/com/eduplatform/common/datasource/EduplatformDataSourceProperties.java` — `@ConfigurationProperties` contract.
- `common/src/main/java/com/eduplatform/common/datasource/EduplatformDataSourceAutoConfiguration.java` — maps shared properties to Boot's datasource configuration.
- `common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` — registers auto-configuration.
- `common/src/test/java/.../EduplatformDataSourcePropertiesTests.java` — binding coverage.
- `api-gateway/pom.xml`, `ApiGatewayApplication.java`, `application.yml`, and a route test — gateway executable and `/auth/**` route.
- `auth-svc/pom.xml`, `AuthSvcApplication.java`, `application.yml`, and a configuration test — JPA/MySQL executable configuration.
- `README.md` — build/run/environment variable instructions.

### Task 1: Make the Maven reactor and common module testable

**Files:**
- Modify: `parent/pom.xml`
- Create: `common/pom.xml`
- Create: `common/src/main/java/com/eduplatform/common/datasource/EduplatformDataSourceProperties.java`
- Create: `common/src/main/java/com/eduplatform/common/datasource/EduplatformDataSourceAutoConfiguration.java`
- Create: `common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Create: `common/src/test/java/com/eduplatform/common/datasource/EduplatformDataSourcePropertiesTests.java`

**Interfaces:**
- Produces: configuration prefix `eduplatform.datasource` with `url`, `username`, and `password`; all service POMs inherit `org.eduplatform:parent:0.0.1-SNAPSHOT`.

- [ ] **Step 1: Write `EduplatformDataSourcePropertiesTests`**

Bind `eduplatform.datasource.url=jdbc:mysql://db:3306/edu`, username and a password containing `!$`; assert all three values are preserved.

- [ ] **Step 2: Run the common test to verify it fails**

Run: `./mvnw -pl ../common test`

Expected: FAIL because module/classes do not exist.

- [ ] **Step 3: Implement shared properties and auto-configuration**

Create `EduplatformDataSourceProperties` with `@ConfigurationProperties("eduplatform.datasource")`; implement `EduplatformDataSourceAutoConfiguration` that supplies the effective Spring datasource properties from it, and register it in `AutoConfiguration.imports`.

- [ ] **Step 4: Convert `parent/pom.xml` into the reactor parent**

Add `../common`, `../api-gateway`, and `../auth-svc` modules. Import the Cloud BOM in `dependencyManagement`, set Java 21, and configure compiler/Spring Boot plugins centrally. Do not declare application dependencies in the parent.

- [ ] **Step 5: Run the common test**

Run: `./mvnw -pl ../common test`

Expected: PASS.

- [ ] **Step 6: Commit**

`git add parent/pom.xml common && git commit -m "feat: add common datasource module"`

### Task 2: Build the API gateway executable

**Files:**
- Modify: `api-gateway/pom.xml`
- Modify: `api-gateway/src/main/java/org/store/apigateway/ApiGatewayApplication.java`
- Modify: `api-gateway/src/main/resources/application.yaml`
- Create: `api-gateway/src/test/java/org/store/apigateway/GatewayRouteTests.java`

**Interfaces:**
- Consumes: `org.eduplatform:common` and the parent POM.
- Produces: a WebFlux gateway on port `6868` with gateway route ID `auth-svc`, predicate `Path=/auth/**`, and URI `http://localhost:45900`.

- [ ] **Step 1: Write `GatewayRouteTests`**

Load only the gateway route configuration and assert route `auth-svc` has URI `http://localhost:45900` and a `/auth/**` path predicate.

- [ ] **Step 2: Run the gateway test to verify it fails**

Run: `./mvnw -pl ../api-gateway test`

Expected: FAIL because Cloud Gateway dependencies/configuration are absent.

- [ ] **Step 3: Implement gateway POM and YAML**

Make the module inherit from `parent`; add `common`, `spring-cloud-starter-gateway-server-webflux`, Lombok, actuator, and test dependencies. Configure `server.port: 6868` and route `auth-svc` in YAML.

- [ ] **Step 4: Run gateway test and dependency guard**

Run: `./mvnw -pl ../api-gateway test dependency:tree -Dincludes=com.mysql:mysql-connector-j,org.springframework.boot:spring-boot-starter-data-jpa`

Expected: tests PASS; neither prohibited dependency appears.

- [ ] **Step 5: Commit**

`git add api-gateway && git commit -m "feat: configure api gateway"`

### Task 3: Build the auth service executable

**Files:**
- Modify: `auth-svc/pom.xml`
- Modify: `auth-svc/src/main/java/org/store/authsvc/AuthSvcApplication.java`
- Modify: `auth-svc/src/main/resources/application.yaml`
- Modify: `auth-svc/src/test/java/org/store/authsvc/AuthSvcApplicationTests.java`

**Interfaces:**
- Consumes: `eduplatform.datasource.*` from `common` and parent-managed versions.
- Produces: an executable JAR that binds to port `45900`, uses MySQL/JPA, and exposes Actuator health when its database is configured.

- [ ] **Step 1: Replace the full-context smoke test with a configuration binding test**

Set only `eduplatform.datasource` test values and assert `server.port` is `45900`; do not boot JPA or require MySQL during Maven tests.

- [ ] **Step 2: Run the auth test to verify it fails**

Run: `./mvnw -pl ../auth-svc test`

Expected: FAIL because the shared configuration/dependencies are not wired.

- [ ] **Step 3: Implement auth service POM and YAML**

Inherit `parent`; add `common`, Spring Web, Data JPA, MySQL Connector/J (runtime), Lombok, Actuator and tests. Configure port `45900`, env-backed `eduplatform.datasource` defaults, JPA dialect/DDL policy, and health endpoints without adding H2.

- [ ] **Step 4: Run the auth test**

Run: `./mvnw -pl ../auth-svc test`

Expected: PASS with no MySQL server running.

- [ ] **Step 5: Commit**

`git add auth-svc && git commit -m "feat: configure auth service"`

### Task 4: Verify and package the project

**Files:**
- Create: `README.md`
- Create: `eduplatform-be.zip` outside the project directory

**Interfaces:**
- Consumes: reactor and all module outputs.
- Produces: build/run documentation and a distributable archive excluding `.idea` and `target` directories.

- [ ] **Step 1: Document build/run configuration**

Document `parent/mvnw clean package`, JAR locations, ports `6868`/`45900`, mandatory auth datasource environment variables, and the gateway `/auth/**` route.

- [ ] **Step 2: Run full reactor verification**

Run: `./mvnw clean package` from `parent`.

Expected: PASS and artifacts for `common`, `api-gateway`, and `auth-svc` are created.

- [ ] **Step 3: Create and inspect archive**

Create `eduplatform-be.zip` at `D:\VanNT_Shared\vannt shop\eduplatform-be.zip`; list its entries to verify source/configuration/docs are included while `.idea` and `target` are excluded.

- [ ] **Step 4: Commit source and docs**

`git add README.md docs && git commit -m "docs: add backend bootstrap instructions"`

## Self-Review

- Spec coverage: Tasks 1–4 cover all required modules, Java/Maven/JAR/YAML, MySQL/JPA/Lombok/Cloud dependencies, ports, build and ZIP.
- Step scan: each task has a focused test/fail/implement/pass cycle and its own verification.
- Type consistency: only shared configuration prefix is `eduplatform.datasource`; both common and auth use it.
- Review focus: all five listed failure modes are assigned to Tasks 1–4.
- Proportion: the plan specifies interfaces and checks without reproducing implementation bodies.

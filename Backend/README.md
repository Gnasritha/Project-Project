# Momthathel — Camunda 8.9 Application

**Aaseya** | Spring Boot 4.0 · Camunda 8.9 · Java 22 · PostgreSQL · Keycloak

---

## Project Overview

Momthathel is a business process automation application built on **Camunda 8.9**
using the **Zeebe job-worker model** (no embedded engine — Camunda runs as a
separate self-managed or SaaS cluster). Mobile-app authentication is delegated
to **Keycloak** via a single-API ROPC bridge.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 22 |
| Framework | Spring Boot 4.0.0 |
| Workflow Engine | Camunda 8.9 (self-managed or SaaS) |
| Camunda Client | `io.camunda:camunda-spring-boot-starter:8.9.0` |
| Identity & Auth | Keycloak (OIDC) — JWTs validated by Spring's OAuth2 Resource Server |
| Database | PostgreSQL (`momthathel` schema) |
| Schema Mgmt | Liquibase (`db/changelog/db.changelog-master.yaml`) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation 3.x |
| Boilerplate | Lombok |
| Build | Maven 3.9+ |

---

## Package Structure

```
com.aaseya.momthathel
├── config/         Spring @Configuration beans (OpenApiConfig, WebConfig)
├── controller/     REST Controllers (@RestController)
├── dao/            Custom @Repository classes (EntityManager / Criteria / JPQL)
├── dto/            Request / Response DTOs + JPQL projection records
├── exception/      Typed exceptions + GlobalExceptionHandler (@RestControllerAdvice)
├── keycloak/       SecurityConfig (OAuth2 Resource Server wiring)
├── model/          JPA @Entity classes mapped to PostgreSQL tables
├── service/        Business logic + CamundaService (process start, task complete)
└── worker/         Camunda @JobWorker handlers (auto-polled by the client)
```

---

## Prerequisites

### 1. Java 22
```bash
java -version   # should show 22.x
```

### 2. PostgreSQL — create the database
```sql
CREATE DATABASE momthathel;
-- grant your user access if needed
```

### 3. Keycloak — for mobile-app login
Bring up Keycloak with the realm shipped in
`keycloak-realms/momthathel-keycloak-realm.json`:

```bash
docker run --name momthathel-keycloak -p 18080:8080 \
  -v "$(pwd)/keycloak-realms:/opt/keycloak/data/import:ro" \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:24.0.5 \
  start-dev --import-realm
```

See [keycloak-realms/README.md](keycloak-realms/README.md) for the realm
contents, seeded users, and the idempotent `import-users.sh` alternative.

### 4. Camunda 8.9 — Self-Managed (Docker Compose) — optional

`camunda.client.enabled` is `false` in `application.yml` by default — the
app boots without a Camunda cluster. To enable Zeebe workers + process
start, bring the stack up:

```bash
git clone https://github.com/camunda/camunda-platform
cd camunda-platform
docker-compose up -d
```

Services started:
- Zeebe (gRPC `:26500`, REST `:8080`)
- Operate (`http://localhost:8081`)
- Tasklist (`http://localhost:8082`)
- Keycloak for Camunda (`http://localhost:18080`)  ← **separate** from the app's Keycloak
- Elasticsearch (`:9200`)

Then flip `camunda.client.enabled: true` in `application.yml`.

---

## Running the Application

### Local (self-managed)
```bash
# Defaults in application.yml already match the shipped realm
# (realm: momthathel, client: momthathel-mobile, role: INSPECTOR,
#  Keycloak at http://localhost:18080). Only override if you're
# pointing at a shared dev Keycloak.

# Database (default password works for local docker postgres)
export DB_PASSWORD=Postgres

# Camunda (only when you've enabled the client)
export CAMUNDA_CLIENT_SECRET=secret

mvn spring-boot:run
# or
java -jar target/momthathel-1.0.0.jar
```

App listens on `http://localhost:8989`.
Swagger UI: `http://localhost:8989/swagger-ui.html`.

### SaaS (Camunda Cloud)
```bash
export CAMUNDA_CLIENT_ID=<your-client-id>
export CAMUNDA_CLIENT_SECRET=<your-client-secret>
export CAMUNDA_CLUSTER_ID=<your-cluster-id>
export CAMUNDA_REGION=bru-2   # or your region

mvn spring-boot:run -Dspring-boot.run.profiles=saas
```

---

## Authentication — single-API Keycloak login

The mobile login screen ("ID Number / Mobile" + "Password") hits one
backend endpoint:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username":   "<ID number, mobile, or Keycloak username>",
  "password":   "<password>",
  "language":   "ar",        // optional
  "rememberMe": true         // optional, accepted but unused server-side
}
```

### What the backend does (single round trip to Keycloak)

1. Sends the credentials to Keycloak's token endpoint
   (`POST {keycloak.server-url}/realms/{realm}/protocol/openid-connect/token`,
   `grant_type=password`).
2. Decodes the returned JWT claims and verifies the realm role
   (default: `INSPECTOR` — `ROLE_INSPECTOR` is also accepted for legacy
   compatibility).
3. Upserts a local `users` row keyed by the JWT `sub` claim
   (`keycloak_sub`). Missing local `Role` row? Auto-created — so a brand
   new Keycloak user can log in against an unseeded `roles` table.
4. Returns the Keycloak access token + the local profile fields the
   downstream mobile screens need.

```json
{
  "accessToken":   "eyJhbGc...",
  "tokenType":     "Bearer",
  "expiresIn":     1800,
  "roles":         ["INSPECTOR"],

  "userId":        42,
  "username":      "inspector01",
  "fullName":      "Ahmed Al-Rashid",
  "employeeId":    "EMP-1001",
  "roleName":      "INSPECTOR",
  "language":      "ar",

  "authToken":     "eyJhbGc...",       // alias of accessToken (legacy)
  "tokenExpiresAt": "2026-05-18T14:25:39",
  "success": true,
  "message": "Login successful"
}
```

Subsequent calls to `/api/mobile/**` send the token as
`Authorization: Bearer <accessToken>`; Spring Security validates the
signature against Keycloak's JWKS.

### Error responses (mapped by `GlobalExceptionHandler`)

| Scenario | HTTP | Body |
|---|---|---|
| Wrong username or password | 401 | `{ "error": "invalid_credentials", "message": "...", "status": 401, "timestamp": "..." }` |
| Authenticated but lacks INSPECTOR role | 403 | `{ "error": "forbidden", "message": "User is not authorised (missing role ROLE_INSPECTOR)", "status": 403, ... }` |
| Empty/missing required fields | 400 | `{ "error": "validation_failed", "message": "username: username is required", "status": 400, ... }` |
| Keycloak unreachable / 5xx | 500 | `{ "error": "internal_error", "message": "...", "status": 500, ... }` |

### Keycloak client requirements

The Keycloak client referenced by `keycloak.client.id` must have:
- `directAccessGrants: true` (enables the password grant)
- `publicClient: true` (or `serviceAccountsEnabled: true` and a client
  secret supplied via `KEYCLOAK_CLIENT_SECRET`)
- Users with realm role `INSPECTOR` (preferred) or `ROLE_INSPECTOR`

### Defaults match the shipped realm

`application.yml` defaults align with `keycloak-realms/momthathel-keycloak-realm.json`:

| Property | Default | Source |
|---|---|---|
| `keycloak.server-url`    | `http://localhost:18080` | local docker Keycloak (port mapped in `docker run`) |
| `keycloak.realm`         | `momthathel` | realm name in the shipped JSON |
| `keycloak.client.id`     | `momthathel-mobile` | public client with `directAccessGrants=true` |
| `keycloak.client.secret` | *empty* | client is public — no secret needed |
| `keycloak.required-role` | `INSPECTOR` | realm role assigned to `inspector01..05` |

Override via env vars (e.g. in `.env`) to point at a shared dev Keycloak,
a SaaS Keycloak, or a differently-named realm:

```bash
KEYCLOAK_SERVER_URL=http://10.13.1.180:8180
KEYCLOAK_REALM=...
KEYCLOAK_CLIENT_ID=...
KEYCLOAK_REQUIRED_ROLE=...
KEYCLOAK_CLIENT_SECRET=...   # only if the client is confidential
```

`AuthService` accepts `INSPECTOR` and `ROLE_INSPECTOR` interchangeably,
so an older `.env` with the legacy `ROLE_` prefix still works.

### `/api/auth/me` and `/api/auth/logout`

- `GET /api/auth/me` — returns the same `LoginResponse` shape for the
  current bearer token (handy for client refresh of profile fields).
- `POST /api/auth/logout` — no-op. JWTs are stateless; the mobile app
  discards the token. Endpoint exists for contract compatibility.

---

## Configuration Summary (`application.yml`)

### PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:Momthathel}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:Postgres}
```

### Keycloak (mobile-app login + JWT validation)
```yaml
keycloak:
  server-url:    ${KEYCLOAK_SERVER_URL:http://10.13.1.180:8180}
  realm:         ${KEYCLOAK_REALM:Inspection Solution Modernization}
  required-role: ${KEYCLOAK_REQUIRED_ROLE:ROLE_INSPECTOR}
  client:
    id:     ${KEYCLOAK_CLIENT_ID:inspection-mobile-app}
    secret: ${KEYCLOAK_CLIENT_SECRET:}

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:${KEYCLOAK_SERVER_URL}/realms/${KEYCLOAK_REALM}}
```

### Camunda 8.9 Client
```yaml
camunda:
  client:
    enabled: false              # flip to true once Zeebe + Keycloak are reachable
    mode: self-managed
    rest-address: http://localhost:8080
    grpc-address: http://localhost:26500
    auth:
      method: oidc
      issuer-url: http://localhost:18080/auth/realms/camunda-platform
      client-id: orchestration
      client-secret: ${CAMUNDA_CLIENT_SECRET:secret}
```

> Note: the **app's** Keycloak (mobile login) and **Camunda's** Keycloak
> (Zeebe SDK auth) are independent realms. Camunda uses the
> `camunda-platform` realm on port 18080; the app uses its own realm on
> port 8180 (or wherever `KEYCLOAK_SERVER_URL` points).

---

## Key Camunda 8.8 / 8.9 API Notes

| Area | Change from 8.7 → 8.8+ |
|---|---|
| Import | `io.camunda.client.CamundaClient` (not `io.camunda.zeebe.client`) |
| Auth property | `issuer-url` (not `issuer`) |
| Process filter | `processDefinitionId()` (not `bpmnProcessId()`) |
| Instance getter | `getProcessDefinitionId()` (not `getBpmnProcessId()`) |
| State filter | Use enum values, not plain Strings |
| Complete task | `newCompleteCommand(taskKey).variables(vars).send().join()` |

---

## Building

```bash
mvn clean package -DskipTests
```

Outputs `target/momthathel-1.0.0.jar`. The Spring Boot Maven plugin
repackages it as an executable jar (Lombok excluded from the artifact).

---

## BPMN Deployment

BPMNs live under:
```
src/main/resources/bpmn/
```

…and are auto-deployed at startup by `MomthatheleApplication`'s
`@Deployment(resources = "classpath:bpmn/*.bpmn")` annotation when the
Camunda client is enabled. Each `<zeebe:taskDefinition type="..."/>` in a
BPMN must have a matching `@JobWorker(type = "...")` method in
`worker/InspectionWorkers.java` or the process will hang.

Manual deployment (if needed):
```java
camundaClient.newDeployResourceCommand()
    .addResourceFromClasspath("bpmn/your-process.bpmn")
    .send().join();
```

---

## Running Tests

```bash
mvn test                       # full suite
mvn test -Dtest=ClassName      # single test class
mvn test -Dtest=ClassName#method
```

Tests activate profile `test` (`@ActiveProfiles("test")` on
`BaseIntegrationTest`). The Camunda client is disabled in test context
and `SecurityConfig` is excluded via `@Profile("!test")`. `TestDataHelper`
clears tables and seeds fixtures per test.

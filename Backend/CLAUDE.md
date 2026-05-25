# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Build, run, test (Maven 3.9+, Java 22 runtime — note `pom.xml` `java.version` is `21`; README states 22):

```bash
mvn clean package -DskipTests         # build executable jar (target/momthathel-1.0.0.jar)
mvn spring-boot:run                   # run with default 'local' profile
mvn spring-boot:run -Dspring-boot.run.profiles=saas    # Camunda Cloud
mvn spring-boot:run -Dspring-boot.run.profiles=docker  # in-container

mvn test                              # full test suite — uses H2 + application-test.yml; Camunda client disabled
mvn test -Dtest=ClassName             # one class
mvn test -Dtest=ClassName#method      # one method
```

Server listens on `:8989`. Swagger UI: `http://localhost:8989/swagger-ui.html`. Actuator health at `/actuator/health`.

Camunda artifacts come from Camunda's Artifactory (`https://artifacts.camunda.com/artifactory/zeebe-io/`), **not Maven Central** — see `pom.xml` `<repositories>`.

`-parameters` javac flag is required (set in `pom.xml`) so Camunda `@Variable` can resolve names without explicit annotation values. Don't remove it.

## Architecture

This is a **Camunda 8.9 Zeebe job-worker** application — the workflow engine runs out-of-process (self-managed Docker stack or Camunda SaaS); this Spring Boot app is purely a Zeebe client that hosts job workers and exposes REST APIs.

### Three external systems this app talks to

1. **Zeebe / Camunda 8.9** — `CamundaClient` (auto-configured by `camunda-spring-boot-starter`) deploys BPMNs and polls for jobs. Configured under `camunda.client.*` in `application.yml`.
2. **PostgreSQL** — `Momthathel` schema, owned by **Liquibase**. `spring.jpa.hibernate.ddl-auto=none` is enforced — never let Hibernate mutate the schema; add a new `V0NN__*.yaml` under `src/main/resources/db/changelog/migrations/` and register it in `db.changelog-master.yaml`.
3. **Keycloak** — single source of truth for inspector identity. Realm `Inspection Solution Modernization`, client `inspection-mobile-app`. The app validates JWTs via Spring's OAuth2 resource server (issuer-uri-based JWKS lookup).

### Authentication flow (important, not obvious from code structure)

There are **two distinct token-validation paths**, and confusing them breaks login:

- `POST /api/auth/login` → `AuthService.login()` → `KeycloakUserService.inspectorLogin()` does a **Resource Owner Password Credentials** call to Keycloak's `/protocol/openid-connect/token`. The returned access token's payload is base64-decoded *locally* (signature **not** re-verified here — see `decodeJwtPayload`) only to extract claims for the local-user upsert. The required realm role (default `ROLE_INSPECTOR`, from `keycloak.required-role`) is checked against `realm_access.roles`.
- All other protected endpoints (`/api/mobile/**`) — the Bearer token is validated **by Spring Security** against the Keycloak JWKS (signature + issuer + expiry). `SecurityConfig.jwtAuthenticationConverter()` maps **both** `realm_access.roles` *and* `resource_access.<client-id>.roles` to `ROLE_*` Spring authorities.

`AuthService.upsertLocalUser()` auto-provisions a row in the local `users` table on first login, keyed by `keycloak_sub` (the JWT `sub` claim). This local user row is the FK target for `InspectionCase`, attachments, etc., so it **must** exist for every authenticated inspector. Don't introduce flows that bypass it.

`/api/auth/**` is permitted unauthenticated; `/api/mobile/**` requires JWT; everything else is currently `permitAll()` — see `SecurityConfig.securityFilterChain`. `SecurityConfig` is gated on `@Profile("!test")`.

### BPMN ↔ Java worker contract

BPMNs live in `src/main/resources/bpmn/` and are deployed at startup via `@Deployment(resources = "classpath:bpmn/*.bpmn")` on `MomthatheleApplication`. Each `<zeebe:taskDefinition type="..."/>` in those BPMNs must have a matching `@JobWorker(type = "...")` method in `worker/InspectionWorkers.java` — the strings must match exactly. Most worker bodies are currently stubs that log and return `Map.of()`; replacement bodies should delegate to services (`InspectorVisitService`, `AttachmentService`, `ViolationService`).

BPMNs currently shipped: `PerformInspection.bpmn` (main flow), `AdhocCreateCase.bpmn`, `EnterAndValidateClauses.bpmn`, `IdentifySituationOnNature.bpmn`. New service tasks need worker stubs added to `InspectionWorkers` or the process will hang on activation.

### Camunda client kill switch

`camunda.client.enabled` is currently set to `false` in `application.yml` to isolate startup issues when Zeebe/Keycloak aren't reachable. With it off, `CamundaClient` is **not** wired into the context — controllers that depend on it (e.g. `InspectionProcessController`) use `@Autowired(required = false)` and return HTTP 503 when called. Workers won't poll. Flip to `true` only after confirming Zeebe (`:8080`, `:26500`) and Keycloak (`:18080`, `camunda-platform` realm) are reachable.

Note: there are **two** Keycloak realms involved in a full deployment — the **Camunda-internal** realm at `:18080/auth/realms/camunda-platform` (for the Zeebe client OIDC auth) and the **app's own** realm at `:8180/realms/Inspection Solution Modernization` (for mobile-app inspector login). They are unrelated; both must be up for end-to-end flows.

### DAO pattern

The `dao/` package is a mix: most repositories (e.g. `UserRepository`) are **custom `@Repository` classes** that use `EntityManager` + JPA Criteria API directly, not Spring Data `JpaRepository` interfaces — despite what `README.md` states. When adding queries, follow whichever pattern the existing repository in that file uses; don't convert between the two unless that's the explicit task.

### Package layout

`config/` (Spring config beans), `controller/` (REST), `dao/` (repositories), `dto/` (request/response — never annotate with JPA), `exception/`, `keycloak/` (SecurityConfig — note this is its own package, **not** under `config/`), `model/` (JPA entities), `service/`, `worker/` (Camunda `@JobWorker`s).

## Keycloak realm bootstrap

For local dev, the realm definition lives in `keycloak-realms/momthathel-keycloak-realm.json` (full export — realm `momthathel`, client `momthathel-mobile`, 9 seeded users with password `Password@123`). Bring up Keycloak with `--import-realm` or re-run `keycloak-realms/import-users.sh` (idempotent — skips on 409) to top up an existing realm. See `keycloak-realms/README.md` for details. **The realm name shipped in the JSON (`momthathel`) does not match the default in `application.yml` (`Inspection Solution Modernization`)** — pick one and align both, or override via `KEYCLOAK_REALM`.

## Secrets warning

`src/main/java/com/aaseya/momthathel/OMSConstant.java` contains hardcoded AWS keys, Camunda Cloud client secret, and Keycloak client secret. Treat these as compromised — rotate before any non-local deployment; do not extend this pattern.

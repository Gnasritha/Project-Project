# Momthathel — Camunda 8.9 Application

**Aaseya** | Spring Boot 4.0 · Camunda 8.9 · Java 22 · PostgreSQL

---

## Project Overview

Momthathel is a business process automation application built on **Camunda 8.9**
using the **Zeebe job-worker model** (no embedded engine — Camunda runs as a
separate self-managed or SaaS cluster).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 22 |
| Framework | Spring Boot 4.0.0 |
| Workflow Engine | Camunda 8.9 (self-managed or SaaS) |
| Camunda Client | `io.camunda:camunda-spring-boot-starter:8.9.0` |
| Database | PostgreSQL (`momthathel` schema) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation 3.x |
| Boilerplate | Lombok |
| Build | Maven 3.9+ |

---

## Package Structure

```
com.aaseya.momthathel
├── config/         Spring @Configuration beans (AppConfig, etc.)
├── controller/     REST Controllers (@RestController)
├── dao/            Spring Data JPA Repositories (interfaces extending JpaRepository)
├── dto/            Request / Response DTOs (no JPA annotations)
├── exception/      Custom exceptions + GlobalExceptionHandler
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

### 3. Camunda 8.9 — Self-Managed (Docker Compose)

Use the official Camunda Platform Docker Compose:
```bash
git clone https://github.com/camunda/camunda-platform
cd camunda-platform
docker-compose up -d
```

Services started:
- Zeebe (gRPC `:26500`, REST `:8080`)
- Operate (`http://localhost:8081`)
- Tasklist (`http://localhost:8082`)
- Keycloak (`http://localhost:18080`)
- Elasticsearch (`:9200`)

---

## Running the Application

### Local (self-managed)
```bash
# Set your Keycloak client secret (default: 'secret' for demo stacks)
export CAMUNDA_CLIENT_SECRET=secret
export DB_PASSWORD=postgres

mvn spring-boot:run
# or
java -jar target/momthathel-1.0.0.jar
```

### SaaS (Camunda Cloud)
```bash
export CAMUNDA_CLIENT_ID=<your-client-id>
export CAMUNDA_CLIENT_SECRET=<your-client-secret>
export CAMUNDA_CLUSTER_ID=<your-cluster-id>
export CAMUNDA_REGION=bru-2   # or your region

mvn spring-boot:run -Dspring-boot.run.profiles=saas
```

---

## Configuration Summary (`application.yml`)

### PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/momthathel
    username: postgres
    password: postgres
```

### Camunda 8.9 Client
```yaml
camunda:
  client:
    mode: self-managed
    rest-address: http://localhost:8080
    auth:
      method: oidc
      issuer-url: http://localhost:18080/auth/realms/camunda-platform
      client-id: orchestration
      client-secret: ${CAMUNDA_CLIENT_SECRET:secret}
```

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

---

## BPMN Deployment

Place your `.bpmn` files under:
```
src/main/resources/bpmn/
```

Deploy via `CamundaClient`:
```java
camundaClient.newDeployResourceCommand()
    .addResourceFromClasspath("bpmn/your-process.bpmn")
    .send().join();
```

---

## Running Tests

```bash
mvn test
# Uses H2 in-memory DB (application-test.yml), Camunda client is disabled
```

# Momthathel — Keycloak realm setup

Mirrors the LMS pattern: one full realm-export JSON for first-time bring-up, and a bash script for incremental user/group import on existing realms.

## Files

| File | Purpose |
|---|---|
| `momthathel-keycloak-realm.json` | Full realm export — realm `momthathel`, client `momthathel-mobile`, 4 roles, 4 groups, 9 users. Import once on a fresh Keycloak (via Admin UI, `--import-realm`, or the realm-import API). |
| `import-users.sh` | Bash + curl script. Re-runnable. Creates groups + roles + users incrementally and skips anything that already exists. Use this to top up an existing realm. |

## What gets created

### Roles (realm-level)
| Role | Purpose |
|---|---|
| `ADMIN` | Full access to all APIs (admin endpoints) |
| `INSPECTOR` | Field inspector — primary mobile-app persona (ISM-2 → ISM-8) |
| `SUPERVISOR` | Reviews / approves cases (planned, not yet wired in the BPMN) |
| `VIEWER` | Read-only access for reporting |

### Groups (group-to-role mapping)
- `GROUP-Admin` → `ADMIN`
- `GROUP-Inspector` → `INSPECTOR`
- `GROUP-Supervisor` → `SUPERVISOR`
- `GROUP-Viewer` → `VIEWER`

### Users (all with password `Password@123`)
| Username | Name | Group |
|---|---|---|
| `admin` | System Administrator | GROUP-Admin |
| `inspector01` | Ahmed Al-Rashid | GROUP-Inspector |
| `inspector02` | Fahad Al-Otaibi | GROUP-Inspector |
| `inspector03` | Khalid Al-Sulaiman | GROUP-Inspector |
| `inspector04` | Mohammed Al-Qahtani | GROUP-Inspector |
| `inspector05` | Saud Al-Ghamdi | GROUP-Inspector |
| `supervisor01` | Sanjay Jain | GROUP-Supervisor |
| `supervisor02` | Jyothsna Valasala | GROUP-Supervisor |
| `viewer01` | Reports User | GROUP-Viewer |

User custom attributes (`employee_id`, `mobile_number`, `language`) line up with what the Spring Boot `users` table expects — easier to keep the two in sync.

---

## Option A — Fresh Keycloak (import the realm once)

If you're bringing Keycloak up for the first time, the easiest path:

```bash
docker run --name momthathel-keycloak -p 18080:8080 \
  -v "$(pwd)/keycloak-realms:/opt/keycloak/data/import:ro" \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:24.0.5 \
  start-dev --import-realm
```

On startup Keycloak picks up every `*.json` in `/opt/keycloak/data/import` and creates the realm + all 9 users.

After it's up:
- Admin console: http://localhost:18080/auth/admin/ (or `/admin/` on newer versions) — login `admin` / `admin`
- Realm: switch top-left dropdown to `momthathel`

## Option B — Existing Keycloak (use the bash script)

If Keycloak is already running and you just want to add users:

```bash
# Pre-req: the 'momthathel' realm must already exist in Keycloak.
#         (Easiest: import momthathel-keycloak-realm.json via the Admin UI
#          → Create Realm → Browse… and pick the file.)

cd keycloak-realms
ADMIN_PASS="admin" bash import-users.sh
```

Override defaults via env vars:
```bash
KEYCLOAK_URL="http://localhost:18080" \
KEYCLOAK_ADMIN_USERNAME="admin" \
ADMIN_PASS="MySecurePass" \
bash import-users.sh
```

The script is idempotent — every step skips on `409 Conflict`, so re-running is safe.

---

## Verifying it works

After import, fetch a JWT for the field inspector:

```bash
curl -X POST http://localhost:18080/realms/momthathel/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=momthathel-mobile" \
  -d "username=inspector01" \
  -d "password=Password@123"
```

You'll get back:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIs...",
  "expires_in": 1800,
  "refresh_token": "...",
  "token_type": "Bearer",
  ...
}
```

Decode that JWT (jwt.io) — `realm_access.roles` will include `INSPECTOR`. Our [SecurityConfig.java](../src/main/java/com/aaseya/momthathel/keycloak/SecurityConfig.java) then maps that to a Spring `ROLE_INSPECTOR` authority, so `@PreAuthorize("hasRole('INSPECTOR')")` works on any controller method.

## How this lines up with the Spring Boot side

| Setting | Value |
|---|---|
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` in [application.yml](../src/main/resources/application.yml) | `http://localhost:18080/realms/momthathel` |
| `keycloak.client-id` in [application.yml](../src/main/resources/application.yml) | `momthathel-mobile` |
| Roles checked in code (when added) | `@PreAuthorize("hasRole('INSPECTOR')")`, `hasRole('ADMIN')`, etc. |

Once Keycloak is up + this realm imported, all you need to flip is:
1. In [application.yml](../src/main/resources/application.yml) remove the three `spring.autoconfigure.exclude` lines (the security excludes that keep dev easy)
2. In [SecurityConfig.java](../src/main/java/com/aaseya/momthathel/keycloak/SecurityConfig.java) change `@Profile("!test & !local")` → `@Profile("!test")`
3. Restart Spring Boot

Then every `/api/mobile/**` request must carry a Keycloak JWT with the right role.

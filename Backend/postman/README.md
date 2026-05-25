# Momthathel — Postman testing bundle

Two files in this folder:

| File | What it is |
|---|---|
| `Momthathel.postman_collection.json` | The 20-endpoint Postman collection, organised into 5 folders by ISM story. All variables live on the collection — no separate environment needed. |
| `seed-data.sql`                      | One-time SQL to insert the establishment / license the collection assumes (user is auto-provisioned from Keycloak on first login) |

## How to use

### 1. Boot Keycloak + the app
The app now requires a reachable Keycloak (single source of truth for auth).
Default points at the realm in `keycloak-realms/momthathel-keycloak-realm.json`
on `http://10.13.1.180:8180`. Override with env vars if needed:

```bash
# .env (or real env)
KEYCLOAK_SERVER_URL=http://localhost:18080
KEYCLOAK_REALM=Inspection Solution Modernization
KEYCLOAK_CLIENT_ID=inspection-mobile-app
KEYCLOAK_REQUIRED_ROLE=ROLE_INSPECTOR
```

Then:
```bash
# from c:\Users\ashwini.mallela\Desktop\Momthathel
mvn spring-boot:run
```
App listens on `http://localhost:8989`.

### 2. Seed reference data (once)
Open pgAdmin (or any SQL client) connected to the `momthathel` database on
`localhost:5433` and run `seed-data.sql`. Liquibase already created the tables
and seeded lookup data on first boot. The local `users` row is **not** seeded —
it is auto-created on first Keycloak login (see step 4).

### 3. Import into Postman
- Postman → **File → Import** → drop `Momthathel.postman_collection.json`
- No environment to select — the collection ships with its own variable defaults
  (`baseUrl`, `establishmentId=1`, `licenseId=1`, `licenseNumber=43054086888`, etc.).
- Edit them via **Collection → Variables** if your seed data differs.

### 4. Run requests top-to-bottom
Collection-level Bearer auth is set to `{{authToken}}`, so every request
inherits the JWT automatically (except `1.1 Login`, which is `noauth`).

The Test scripts auto-save (into collection variables):
- `authToken`    + `inspectorId` after **1.1 Login**
- `inspectionId` after **2.4 Create Visit**
- `attachmentId` after **4.1 Upload Attachment**
- `violationId`  after **5.2 Add Violation**

So you can fire the requests in order and don't need to copy-paste IDs.

**Default login credentials** (from `momthathel-keycloak-realm.json`):
- username: `inspector`
- password: `inspector123`

The user must have realm role `ROLE_INSPECTOR`. On first successful login the
backend auto-provisions a local `users` row from the JWT claims and stores its
`user_id` in `{{inspectorId}}`. To onboard a new inspector, create them in
Keycloak (with `ROLE_INSPECTOR`) — no DB changes required.

### 5. For multipart upload (4.1)
- Open the request, Body tab → form-data
- Click the empty cell next to `file`, switch the type dropdown to **File**, select any file from disk

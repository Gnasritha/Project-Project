# Momthathel — Postman testing bundle

Three files in this folder:

| File | What it is |
|---|---|
| `Momthathel.postman_collection.json` | The 20-endpoint Postman collection, organised into 5 folders by ISM story |
| `Momthathel.postman_environment.json` | Local-dev environment variables (`baseUrl`, IDs, token slots) |
| `seed-data.sql`                       | One-time SQL to insert the user / establishment / license the collection assumes |

## How to use

### 1. Boot the app
```bash
# from c:\Users\ashwini.mallela\Desktop\Momthathel
mvn spring-boot:run
```
App listens on `http://localhost:8989` with `local` profile (no Keycloak / Camunda required).

### 2. Seed the data (once)
Open pgAdmin (or any SQL client) connected to the `momthathel` database on `localhost:5433` and run `seed-data.sql`. Liquibase already created the tables and seeded the lookup tables on first boot.

### 3. Import into Postman
- Postman → **File → Import** → drop both JSON files
- Top-right environment dropdown → **Momthathel — Local**

### 4. Run requests top-to-bottom
The Pre-request / Test scripts in the collection auto-save:
- `authToken`         after **1.1 Login** (username `inspector01` / password `Password@123`)
- `inspectionId`      after **2.4 Create Visit**
- `attachmentId`      after **4.1 Upload Attachment**
- `violationId`       after **5.2 Add Violation**

So you can fire the requests in order and don't need to copy-paste IDs.

**Note on the login password:** The seed SQL inserts `inspector01` with a placeholder hash. On first boot, `AuthInitializer` detects the placeholder and rehashes it to bcrypt(`Password@123`). After that, login works with `inspector01` / `Password@123`. To change the default, edit `AuthInitializer.DEFAULT_PASSWORD` and restart, or reset the user's `password_hash` in the DB so the initializer picks it up again on next boot.

### 5. For multipart upload (4.1)
- Open the request, Body tab → form-data
- Click the empty cell next to `file`, switch the type dropdown to **File**, select any file from disk

### When you want to test full security with Keycloak
- Switch `spring.profiles.active` to `docker` or remove the auto-config excludes from `application.yml` `local` profile
- Bring up Keycloak on `:18080` with a `momthathel` realm + `momthathel-mobile` client
- Add `Authorization: Bearer <keycloak-jwt>` to every `/api/mobile/**` request

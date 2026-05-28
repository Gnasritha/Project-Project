-- ============================================================================
-- Momthathel — Postman seed data
-- Run ONCE against your `momthathel` PostgreSQL database before testing.
-- (Liquibase already seeds Roles + InspectionTypes + Clauses + ISIC, so this
--  only adds the user / establishment / license / user→type mapping
--  that the Postman collection assumes exists with ids 1.)
-- ============================================================================

-- ── 1. Test inspector (role_id 2 = INSPECTOR) ─────────────────────────────
INSERT INTO users (user_id, role_id, employee_id, full_name, mobile_number,
                   email, username, password_hash, language, status)
VALUES (nextval('users_user_id_seq'), 2, 'EMP-1001', 'Ahmed Al-Rashid',
        '+966500000001', 'ahmed@aaseya.com', 'inspector01',
        '$2a$10$dummyhashplaceholder', 'en', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- ── 2. Authorise inspector for HEALTH + FOOD inspection types ─────────────
INSERT INTO user_inspection_type (user_id, inspection_type_id)
SELECT u.user_id, it.inspection_type_id
FROM   users u, inspection_type it
WHERE  u.username = 'inspector01'
  AND  it.inspection_code IN ('HEALTH', 'FOOD')
ON CONFLICT DO NOTHING;

-- ── 3. Establishment ──────────────────────────────────────────────────────
INSERT INTO establishment (establishment_id, establishment_name, activity_type,
                           address, city, latitude, longitude, license_available,
                           national_facility_number)
VALUES (nextval('establishment_establishment_id_seq'),
        'Al-Rashid Trading Co.', 'Retail',
        'King Fahd Road, Block 4', 'Riyadh',
        24.7136, 46.6753, true,
        '7027127898');

-- ── 4. License attached to that establishment ─────────────────────────────
INSERT INTO license (license_id, license_number, establishment_id,
                     issue_date, expiry_date, license_status, license_type)
SELECT nextval('license_license_id_seq'), '43054086888',
       e.establishment_id, '2024-01-15', '2027-01-14',
       'ACTIVE', 'Commercial License'
FROM   establishment e
WHERE  e.establishment_name = 'Al-Rashid Trading Co.'
ON CONFLICT (license_number) DO NOTHING;

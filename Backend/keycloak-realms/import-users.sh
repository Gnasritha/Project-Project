#!/bin/bash

# ============================================================================
# Import Inspector User into Inspection Solution Modernization Realm
# ============================================================================

KEYCLOAK_URL="${KEYCLOAK_URL:-http://10.13.1.180:8180}"
REALM="Inspection Solution Modernization"

ADMIN_USER="${KEYCLOAK_ADMIN_USERNAME:-admin}"
ADMIN_PASS="${KEYCLOAK_ADMIN_PASSWORD:-admin}"

echo "Keycloak URL : $KEYCLOAK_URL"
echo "Realm        : $REALM"
echo "Admin User   : $ADMIN_USER"

echo ""
echo "=== STEP 1 : GET ADMIN TOKEN ==="

TOKEN=$(curl -s -X POST \
"${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "client_id=admin-cli" \
-d "username=${ADMIN_USER}" \
-d "password=${ADMIN_PASS}" \
-d "grant_type=password")

ACCESS_TOKEN=$(echo "$TOKEN" | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//')

if [ -z "$ACCESS_TOKEN" ]; then
  echo "ERROR : Failed To Get Admin Token"
  exit 1
fi

echo "Admin Token Generated Successfully"

# ============================================================================
# STEP 2 : CREATE GROUP
# ============================================================================

echo ""
echo "=== STEP 2 : CREATE GROUP ==="

GROUP_RESULT=$(curl -s -o /dev/null -w "%{http_code}" \
-X POST \
"${KEYCLOAK_URL}/admin/realms/${REALM}/groups" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
-H "Content-Type: application/json" \
-d '{
"name":"GROUP-Inspector"
}')

if [ "$GROUP_RESULT" = "201" ]; then
  echo "GROUP-Inspector Created"
elif [ "$GROUP_RESULT" = "409" ]; then
  echo "GROUP-Inspector Already Exists"
else
  echo "Group Creation Failed : HTTP $GROUP_RESULT"
fi

# ============================================================================
# STEP 3 : CREATE ROLE
# ============================================================================

echo ""
echo "=== STEP 3 : CREATE ROLE ==="

ROLE_RESULT=$(curl -s -o /dev/null -w "%{http_code}" \
-X POST \
"${KEYCLOAK_URL}/admin/realms/${REALM}/roles" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
-H "Content-Type: application/json" \
-d '{
"name":"ROLE_INSPECTOR"
}')

if [ "$ROLE_RESULT" = "201" ]; then
  echo "ROLE_INSPECTOR Created"
elif [ "$ROLE_RESULT" = "409" ]; then
  echo "ROLE_INSPECTOR Already Exists"
else
  echo "Role Creation Failed : HTTP $ROLE_RESULT"
fi

# ============================================================================
# STEP 4 : ASSIGN ROLE TO GROUP
# ============================================================================

echo ""
echo "=== STEP 4 : ASSIGN ROLE TO GROUP ==="

GROUP_ID=$(curl -s \
"${KEYCLOAK_URL}/admin/realms/${REALM}/groups?search=GROUP-Inspector" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
| grep -o '"id":"[^"]*"' \
| head -1 \
| sed 's/"id":"//;s/"//')

ROLE_JSON=$(curl -s \
"${KEYCLOAK_URL}/admin/realms/${REALM}/roles/ROLE_INSPECTOR" \
-H "Authorization: Bearer ${ACCESS_TOKEN}")

ROLE_ASSIGN_RESULT=$(curl -s -o /dev/null -w "%{http_code}" \
-X POST \
"${KEYCLOAK_URL}/admin/realms/${REALM}/groups/${GROUP_ID}/role-mappings/realm" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
-H "Content-Type: application/json" \
-d "[${ROLE_JSON}]")

echo "Role Assigned : HTTP $ROLE_ASSIGN_RESULT"

# ============================================================================
# STEP 5 : CREATE INSPECTOR USER
# ============================================================================

echo ""
echo "=== STEP 5 : CREATE INSPECTOR USER ==="

USER_RESULT=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
-X POST \
"${KEYCLOAK_URL}/admin/realms/${REALM}/partialImport" \
-H "Authorization: Bearer ${ACCESS_TOKEN}" \
-H "Content-Type: application/json" \
-d '{
  "ifResourceExists":"SKIP",

  "users":[
    {
      "username":"inspector",

      "firstName":"Inspection",

      "lastName":"Inspector",

      "email":"inspector@aaseya.com",

      "enabled":true,

      "emailVerified":true,

      "credentials":[
        {
          "type":"password",
          "value":"inspector123",
          "temporary":false
        }
      ],

      "groups":[
        "GROUP-Inspector"
      ],

      "realmRoles":[
        "ROLE_INSPECTOR"
      ]
    }
  ]
}')

HTTP_STATUS=$(echo "$USER_RESULT" | grep "HTTP_STATUS:" | sed 's/HTTP_STATUS://')

if [ "$HTTP_STATUS" = "200" ]; then

  echo ""
  echo "========================================="
  echo "INSPECTOR USER CREATED SUCCESSFULLY"
  echo "========================================="

  echo ""
  echo "USERNAME : inspector"
  echo "PASSWORD : inspector123"

  echo ""
  echo "REALM : ${REALM}"

  echo ""
  echo "TEST LOGIN API :"

  echo "curl -X POST ${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token \\"

  echo "-H \"Content-Type: application/x-www-form-urlencoded\" \\"

  echo "-d \"grant_type=password\" \\"

  echo "-d \"client_id=inspection-mobile-app\" \\"

  echo "-d \"username=inspector\" \\"

  echo "-d \"password=inspector123\""

else

  echo "User Creation Failed"

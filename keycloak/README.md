# Keycloak setup

This project uses a standalone Keycloak server (not Docker) for customer login / JWT auth on `api-gateway`.

## One-time setup on a fresh machine

1. Download and extract Keycloak 26.7.0: https://github.com/keycloak/keycloak/releases/download/26.7.0/keycloak-26.7.0.tar.gz
2. Requires Java 17 or 21 (not newer — Keycloak 26.x doesn't support Java 25 yet).
3. Import this realm on first startup:

```
KC_BOOTSTRAP_ADMIN_USERNAME=admin KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  bin/kc.sh start-dev --http-port=8180 --import-realm
```

   Copy `banking-realm.json` into `<keycloak-dir>/data/import/` before running the above — Keycloak auto-imports any realm file placed there on startup.

   Alternatively, import via the Admin Console UI: `http://localhost:8180/admin` → Realm dropdown → Create Realm → Browse → select `banking-realm.json`.

## What's in the realm

- Realm: `banking`
- Client: `banking-app` (public client, Direct Access Grant enabled, Standard Flow disabled — built for Postman/API testing via username+password, not browser redirect login)
- Role: `CUSTOMER`
- Test user: `alice` / `alice123`

## Getting a token (Postman)

```
POST http://localhost:8180/realms/banking/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&client_id=banking-app&username=alice&password=alice123
```

Use the returned `access_token` as `Authorization: Bearer <token>` on requests to `api-gateway` (port 9090).

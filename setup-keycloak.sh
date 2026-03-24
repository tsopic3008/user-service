#!/bin/bash

echo "Setting up Keycloak configuration..."

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
while ! curl -s http://localhost:8080/health > /dev/null; do
    echo "Keycloak not ready yet, waiting..."
    sleep 5
done
echo "Keycloak is ready!"

# Get admin token
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
    echo "Failed to get admin token"
    exit 1
fi
echo "Admin token obtained"

# Create tscore realm
echo "Creating tscore realm..."
curl -s -X POST http://localhost:8080/admin/realms \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "realm": "tscore",
        "enabled": true,
        "displayName": "TScore"
    }'

# Create tscore client
echo "Creating tscore client..."
curl -s -X POST http://localhost:8080/admin/realms/tscore/clients \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "clientId": "tscore",
        "enabled": true,
        "publicClient": false,
        "secret": "J8ZpvHufBATvVCFmvph9SKyCoPbNd6rk",
        "redirectUris": ["http://localhost:4200/*", "http://localhost:8081/*"],
        "webOrigins": ["http://localhost:4200"],
        "standardFlowEnabled": true,
        "directAccessGrantsEnabled": true,
        "serviceAccountsEnabled": true
    }'

# Create testuser
echo "Creating testuser..."
curl -s -X POST http://localhost:8080/admin/realms/tscore/users \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "enabled": true,
        "credentials": [
            {
                "type": "password",
                "value": "Test1!",
                "temporary": false
            }
        ]
    }'

echo "Keycloak setup completed!"
echo "You can now access:"
echo "- Keycloak: http://localhost:8080 (admin/admin)"
echo "- TScore App: http://localhost:8081"
echo "- Test user: testuser/Test1!" 
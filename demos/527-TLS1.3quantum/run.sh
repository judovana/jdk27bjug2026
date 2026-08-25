#!/usr/bin/env bash
set -euo pipefail

# ─── Configuration ────────────────────────────────────────────────────────────
KEYSTORE="server.p12"
TRUSTSTORE="truststore.p12"
PASSWORD="changeit"
ALIAS="server"
PORT=8443
QUANTUM_JDK=/usr/lib/jvm/java-latest-openjdk
OLD_JDK=/usr/lib/jvm/java-21-openjdk
KEY_JDK=$QUANTUM_JDK

echo "=== Step 1: Generate self-signed certificate + keystore ==="
rm -f "$KEYSTORE" "$TRUSTSTORE"

$KEY_JDK/bin/keytool -genkeypair \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=State, C=US" \
  -keystore "$KEYSTORE" \
  -storetype PKCS12 \
  -storepass "$PASSWORD" \
  -keypass "$PASSWORD"

echo "=== Step 2: Export certificate into truststore ==="
$KEY_JDK/bin/keytool -exportcert \
  -alias "$ALIAS" \
  -keystore "$KEYSTORE" \
  -storepass "$PASSWORD" \
  -rfc \
  -file server.crt

$KEY_JDK/bin/keytool -importcert \
  -alias "$ALIAS" \
  -file server.crt \
  -keystore "$TRUSTSTORE" \
  -storetype PKCS12 \
  -storepass "$PASSWORD" \
  -noprompt

rm -f server.crt


echo "=== Step 3: Start server in background ==="
$QUANTUM_JDK/bin/java Server.java &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

# Wait until the server is accepting connections
echo "  Waiting for server..."
sleep 1

echo "=== Step 4: Run client ==="
$OLD_JDK/bin/java Client.java

echo "=== Step 5: Shutdown server ==="
kill "$SERVER_PID" 2>/dev/null
wait "$SERVER_PID" 2>/dev/null
echo "Done."

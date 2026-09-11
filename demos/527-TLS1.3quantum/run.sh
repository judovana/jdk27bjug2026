#!/usr/bin/bash
set -euo pipefail

KEYSTORE="server.p12"
TRUSTSTORE="truststore.p12"
PASSWORD="changeit"
ALIAS="server"
PORT=8443
QUANTUM_JDK=/usr/lib/jvm/java-latest-openjdk
#QUANTUM_JDK=/usr/lib/jvm/java-21-openjdk # will cause reply termination
OLD_JDK=/usr/lib/jvm/java-latest-openjdk
#OLD_JDK=/usr/lib/jvm/java-21-openjdk # will cause invlaid reply
KEY_JDK=/usr/lib/jvm/temurin-8-jdk


rm -f "$KEYSTORE" "$TRUSTSTORE"
$KEY_JDK/bin/keytool -genkeypair   -alias "$ALIAS"   -keyalg RSA   -keysize 2048   -validity 365   -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=State, C=US"   -keystore "$KEYSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -keypass "$PASSWORD"
$KEY_JDK/bin/keytool -exportcert   -alias "$ALIAS"   -keystore "$KEYSTORE"   -storepass "$PASSWORD"   -rfc   -file server.crt
$KEY_JDK/bin/keytool -importcert   -alias "$ALIAS"   -file server.crt   -keystore "$TRUSTSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -noprompt
rm -f server.crt

$QUANTUM_JDK/bin/java Server.java &
SERVER_PID=$!
echo "=== Server PID: $SERVER_PID === "
function killit() {
  echo "=== Shuting down server ==="
  kill "$SERVER_PID" 2>/dev/null
  wait "$SERVER_PID" 2>/dev/null
  echo "Done."
}
trap killit EXIT SIGINT SIGTERM ERR

# Wait until the server is accepting connections
echo "  Waiting for server..."
sleep 1

echo "===  Run client with $OLD_JDK ==="
$OLD_JDK/bin/java Client.java



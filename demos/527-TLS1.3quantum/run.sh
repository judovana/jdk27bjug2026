#!/usr/bin/bash
set -eo pipefail

KEYSTORE="server.p12"
TRUSTSTORE="truststore.p12"
PASSWORD="changeit"
ALIAS="server"
if [ -z $FORCE_PORT ] ; then
  export FORCE_PORT=8443
fi
if [ -z $SERVER_JDK ] ; then
  SERVER_JDK=/usr/lib/jvm/java-latest-openjdk
  #SERVER_JDK=/usr/lib/jvm/java-25-openjdk # will cause reply termination
fi
if [ -z $CLIENT_JDK ] ; then
  CLIENT_JDK=/usr/lib/jvm/java-latest-openjdk
  #CLIENT_JDK=/usr/lib/jvm/java-25-openjdk # will cause invlaid reply
fi
if [ -z $KEY_JDK ] ; then
  KEY_JDK=/usr/lib/jvm/temurin-8-jdk
fi

# pass eg JAVA_TOOL_OPTIONS="$ JAVA_TOOL_OPTIONS -Djavax.net.debug=ssl:handshake+thread+timestamp"
# to see debug also from here

set -u

rm -f "$KEYSTORE" "$TRUSTSTORE"
$KEY_JDK/bin/keytool -genkeypair   -alias "$ALIAS"   -keyalg RSA   -keysize 2048   -validity 365   -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=State, C=US"   -keystore "$KEYSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -keypass "$PASSWORD"
$KEY_JDK/bin/keytool -exportcert   -alias "$ALIAS"   -keystore "$KEYSTORE"   -storepass "$PASSWORD"   -rfc   -file server.crt
$KEY_JDK/bin/keytool -importcert   -alias "$ALIAS"   -file server.crt   -keystore "$TRUSTSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -noprompt
rm -f server.crt

$SERVER_JDK/bin/java Server.java &
SERVER_PID=$!
echo "=== Server PID: $SERVER_PID === "
function killit() {
  set +e
  echo "=== Shuting down server ==="
  kill "$SERVER_PID" 2>/dev/null
  wait "$SERVER_PID" 2>/dev/null
  echo "Done."
  exit $clientExit
}
trap killit EXIT SIGINT SIGTERM ERR

# Wait until the server is accepting connections
echo "  Waiting for server..."
sleep 1

echo "===  Run client with $CLIENT_JDK ==="
clientExit=0;
$CLIENT_JDK/bin/java Client.java || clientExit=$?
exit $clientExit



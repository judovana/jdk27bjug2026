#!/usr/bin/bash
set -euo pipefail

KEYSTORE="server.p12"
TRUSTSTORE="truststore.p12"
PASSWORD="changeit"
ALIAS="server"
PORT=8443
#ahve you killed the server?
SERVER_JDK=/usr/lib/jvm/java-latest-openjdk
#SERVER_JDK=/usr/lib/jvm/java-21-openjdk # will cause reply termination
CLIENT_JDK=/usr/lib/jvm/java-latest-openjdk
#CLIENT_JDK=/usr/lib/jvm/java-21-openjdk # will cause invlaid reply
KEY_JDK=/usr/lib/jvm/temurin-8-jdk


rm -f "$KEYSTORE" "$TRUSTSTORE"
$KEY_JDK/bin/keytool -genkeypair   -alias "$ALIAS"   -keyalg RSA   -keysize 2048   -validity 365   -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=State, C=US"   -keystore "$KEYSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -keypass "$PASSWORD"
$KEY_JDK/bin/keytool -exportcert   -alias "$ALIAS"   -keystore "$KEYSTORE"   -storepass "$PASSWORD"   -rfc   -file server.crt
$KEY_JDK/bin/keytool -importcert   -alias "$ALIAS"   -file server.crt   -keystore "$TRUSTSTORE"   -storetype PKCS12   -storepass "$PASSWORD"   -noprompt
rm -f server.crt

#$SERVER_JDK/bin/java -Djava.security.debug=all+thread+timestamp Server.java &
$SERVER_JDK/bin/java -Djavax.net.debug=ssl:handshake+thread+timestamp Server.java 2>&1 | tee server | grep -ie MLKEM & #grep -ie "named group" &

SERVER_PID=$!
echo "=== Server PID: $SERVER_PID === "
function killit() {
  echo "=== Shuting down server ==="
  echo "debug is preventing"
  echo "jps/ps, kill server!!!"
  kill -9 "$SERVER_PID" 2>/dev/null
  wait "$SERVER_PID" 2>/dev/null
  echo "Done."
}
trap killit EXIT SIGINT SIGTERM ERR

# Wait until the server is accepting connections
echo "  Waiting for server..."
sleep 1

echo "===  Run client with $CLIENT_JDK ==="
#$CLIENT_JDK/bin/java -Djava.security.debug=all+thread+timestamp Client.java
$CLIENT_JDK/bin/java -Djavax.net.debug=ssl:handshake+thread+timestamp Client.java 2>&1 | tee client | grep -ie MLKEM & # grep -ie "named group"

# debug is preventing 
# ps, kill server!!!

 



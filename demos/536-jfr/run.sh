function check() {
  echo $1
  set -x
  cat $1 | grep ANOTHER_SECRET_PASSWORD
  cat $1 | grep SECRET_TOKEN
  cat $1 | grep 12345
  cat $1 | grep 6789
}

export ACCESS_TOKEN=SECRET_TOKEN
export PASWORD=12345

set +x
/usr/lib/jvm/java-21-openjdk/bin/java \
       -XX:StartFlightRecording:filename=dump21.jfr \
       -Xmx2G \
       -Djavax.net.ssl.keyStorePassword=SECRET_PASSWORD \
       ../527-TLS1.3quantum/Client.java \
      --dbpassword ANOTHER_SECRET_PASSWORD \
      --dbpasword 6789 2>&1 | head -n5
check dump21.jfr

set +x
/usr/lib/jvm/java-latest-openjdk/bin/java \
       -XX:StartFlightRecording:filename=dump27_0.jfr \
       -XX:FlightRecorderOptions:'redact-argument=none,redact-key=none' \
       -Xmx2G \
       -Djavax.net.ssl.keyStorePassword=SECRET_PASSWORD \
       ../527-TLS1.3quantum/Client.java \
      --dbpassword ANOTHER_SECRET_PASSWORD \
      --dbpasword 6789 2>&1 | head -n5
check dump27_0.jfr 

set +x
/usr/lib/jvm/java-latest-openjdk/bin/java \
       -XX:StartFlightRecording:filename=dump27_1.jfr \
       -Xmx2G \
       -Djavax.net.ssl.keyStorePassword=SECRET_PASSWORD \
       ../527-TLS1.3quantum/Client.java \
      --dbpassword ANOTHER_SECRET_PASSWORD \
      --dbpasword 6789 2>&1 | head -n5
check dump27_1.jfr 

set +x
/usr/lib/jvm/java-latest-openjdk/bin/java \
       -XX:StartFlightRecording:filename=dump27_2.jfr \
       -XX:FlightRecorderOptions:'redact-key=+*pasword*,redact-argument=+*pasword*' \
       -Xmx2G \
       -Djavax.net.ssl.keyStorePassword=SECRET_PASSWORD \
       ../527-TLS1.3quantum/Client.java \
      --dbpassword ANOTHER_SECRET_PASSWORD \
      --dbpasword 6789 2>&1 | head -n5
check dump27_2.jfr


## [Post-Quantum Hybrid Key Exchange for TLS 1.3](https://openjdk.org/jeps/527)

* old server - willt terminate reply
* old client will not understand
* see the backward compatibility, then it will run wih old client
* note the keytool jdk
* Note the manual restrictiosn in server
 * similar cen be set for client
* sh run.sh

* Variables of 
  * FORCE_PORT
  * SERVER_JDK
  * CLIENT_JDK
  * SERVER_FORCE_CURVES
  * CLIENT_FORCE_CURVES
    * client/server forced curves support "defaults" to do "nothing"
Can be now used to play with curves without touchng the code (and also in default)

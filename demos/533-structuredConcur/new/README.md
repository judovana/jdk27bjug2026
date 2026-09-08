##  533: [Structured Concurrency (Fifth Preview)](https://openjdk.org/jeps/533)

* Demo1_TypedExceptions.java   -   R_X type parameter — join() throws your domain exception directly 
  * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo1_TypedExceptions.java`
* Demo2_OpenWithConfig.java     -   open(UnaryOperator) — configure timeout/threads without a Joiner
  * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo2_OpenWithConfig.java`
* Demo3_ExecutionException.java -  No-arg joiners now throw ExecutionException by default
  * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo3_ExecutionException.java`
* Demo4_AwaitAllSuccessful.java - awaitAllSuccessfulOrThrow() replaces removed awaitAll()
  * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo4_AwaitAllSuccessful.java`
* Demo5_TimeoutMethod.java      - timeout() replaces onTimeout() in custom Joiners
  * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo5_TimeoutMethod.java`





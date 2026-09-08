##  533: [Structured Concurrency (Fifth Preview)](https://openjdk.org/jeps/533)

 * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Demo.java `  without/with exception
 * `/usr/lib/jvm/java-latest-openjdk/bin/java   Main1.java ` - threads
 * `/usr/lib/jvm/java-latest-openjdk/bin/java   Main2.java ` nicer threads
   * ugly, demonstrates verbosity and join
 * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Main3.java ` scopes
   * no more forks after joins, but other threads can
 * `/usr/lib/jvm/java-latest-openjdk/bin/java   Main4.java ` exception in threads
 * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Main5.java ` exception in structured concurency
 * `/usr/lib/jvm/java-latest-openjdk/bin/java  --enable-preview  --source 27 Main6.java ` scoped values in structured concurency
   * see 506-scoped/Framework6.java
 * https://www.youtube.com/watch?v=OcUAyTY2V7g




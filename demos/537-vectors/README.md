## 537: [Vector API (Twelvth Incubator)](https://openjdk.org/jeps/537)

 * `/usr/lib/jvm/java-25-openjdk/bin/java --add-modules jdk.incubator.vector --enable-preview  --source 25  Main1.java`
   * https://docs.oracle.com/en/java/javase/26/docs/api/jdk.incubator.vector/jdk/incubator/vector/package-summary.html#class-summary
   * collections and arrays, primitives and autoboxing, vectors and Vectors(!)
    * Do not mess ancient java.util.Vector with jdk.incubator.vector.Vector
 * `/usr/lib/jvm/java-25-openjdk/bin/java --add-modules jdk.incubator.vector --enable-preview  --source 25  Main2.java`
   * vectors x primitive arrays
   * simple operation
   * One need at leaset 500000 operations to see vectors win (or disable jit by no warmaup and reducing iterations_
 * `/usr/lib/jvm/java-25-openjdk/bin/java --add-modules jdk.incubator.vector --enable-preview  --source 25  Main3.java`
   * vectors x primitive arrays
   * more complex operation
   * Still one need at leaset 500000 operations to see vectors win (or disable jit by no warmaup and reducing iterations_
 * `/usr/lib/jvm/java-25-openjdk/bin/java --add-modules jdk.incubator.vector --enable-preview  --source 25  Main4.java`
   * vectors x primitive arrays
   * very complex operation
   * Here we can see jit's autovectorisation already yield to manual vectors
    * but the api...

 * in 2 and 3
   * show the cpu sensitivity when `stress --cpu 16 --timeout 120`
   * see the non-jitted code like:
```
    max=50000000;
    iter=1; //5!
    warms = 1;
```
   * will suddenly yield (a lot with 5)



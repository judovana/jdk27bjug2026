## 531: [Lazy Constants (Third Preview)](https://openjdk.org/jeps/531)

 * `meld LazyDemo25.java LazyDemo2627.java`
 * `/usr/lib/jvm/java-latest-openjdk/bin/java --enable-preview   --source=25  `
 * `/usr/lib/jvm/java-latest-openjdk-valhalla/bin/java --enable-preview   --source=27 LazyDemo2627.java`

 * newRandom - easy constructor, harder getter
   * build-all.sh
 * newThrowable - hard constructor, easy getter
   * build-all.sh
 * or run just subtasks. Namly:
  * METHOD=".getR()" sh generate.sh final_lazy
  * METHOD=""        sh generate.sh final_lazy
  x 
  * METHOD=".getR()" sh generate.sh final_stable
  * METHOD=""        sh generate.sh final_stable

 * clean:
```
rm -rf  `find -type d | grep -e "/build$"  -e "/build-call$"`
```



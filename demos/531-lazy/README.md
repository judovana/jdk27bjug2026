## 531: [Lazy Constants (Third Preview)](https://openjdk.org/jeps/531)

 * `meld LazyDemo25.java LazyDemo2627.java`
 * `/usr/lib/jvm/java-25-openjdk/bin/java --enable-preview   --source=25  `
 * `/usr/lib/jvm/java-27-openjdk-valhalla/bin/java --enable-preview   --source=27 LazyDemo2627.java`

 * newRandom - easy constructor, harder getter
   * build-all.sh
 * newThrowable - hard constructor, easy getter
   * build-all.sh
 * note various grupped tasks
 * clean:
```
rm -rf  `find -type d | grep -e "/build$"  -e "/build-call$"`
```



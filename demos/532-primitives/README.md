# 532: [Primitive Types in Patterns, instanceof, and switch (Fifth Preview)](https://openjdk.org/jeps/532)

Warning! This demo was AI amended:(

 1. Primitive type patterns in switch — case int i replacing default with value capture, plus guards
 2. Safe narrowing in record patterns — double -> int matching only on exact values
 3. Primitive instanceof with pattern variable — instanceof byte b replacing manual range checks
 4. Bare primitive instanceof — instanceof float as a one-expression exactness test, including across boxing
 5. Switch on boolean/long/float/double — previously impossible selector types
 6. Unconditionally exact conversions — the precision rules that drive exhaustiveness
 7. Exhaustiveness with wrappers — switch(Byte) exhausted by case int p
 8. Dominance checking — compile-time prevention of unreachable patterns
 9. Floating-point case label semantics — representation equivalence, +0/-0, NaN

`/usr/lib/jvm/java-latest-openjdk/bin/java --enable-preview Jep532Demo.java`


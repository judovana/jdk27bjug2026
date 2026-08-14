#          Still Brewing II
###    JDK27 JEPs in nutshell

By Jiri Vanek from IBM's OpenJDK team.
16/9/2026, Brno - JUG meetup in https://impacthub.cz/brno/


Second STS release, year after feature full LTS JDK25

As expected, still brewing

Following https://www.youtube.com/watch?v=ejhok_F3fHg
and https://www.youtube.com/watch?v=UnA2jRVNb3M

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
#  Short reminder of release cycle

3x sts - evey half a year
1x lts - every 2 years
replaced feature driven releases for jdk8 and older. JDK9 was 9/2017, JDK8 was 9/2014 and JDK7 7/2011
Originally the LTS was there every 3 years
There is no lts - lts serves for oracle jdk. Openjdk aligns to it

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# Migration path in Developers minds

  * Who is using STS?
    * daily?
    * as playground?
    * is actually trying the new features?
      * which?
    * is actually using the new features?
      * which?
  * Who is jumping from LTS to another LTS?
    * and just checking the new features passively

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
#  JDK27 (15/9/2027)

527: [Post-Quantum Hybrid Key Exchange for TLS 1.3](https://openjdk.org/jeps/527)
536: [JFR In-Process Data Redaction](https://openjdk.org/jeps/536)

523: [Make G1 the Default Garbage Collector in All Environments](https://openjdk.org/jeps/523)
534: [Compact Object Headers by Default](https://openjdk.org/jeps/534)
    ( 6%, 4th)

531: [Lazy Constants (Third Preview)](https://openjdk.org/jeps/531)
    ( 5%) 6th
532: [Primitive Types in Patterns, instanceof, and switch (Fifth Preview)](https://openjdk.org/jeps/532)
533: [Structured Concurrency (Seventh Preview)](https://openjdk.org/jeps/533)
537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537)
   ( 6% 5th)
538: [PEM Encodings of Cryptographic Objects (Third Preview)](https://openjdk.org/jeps/538)

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# Swing is not dead!
Používateľské rozhrania a desktopové aplikácie napísané pred viac ako dvadsiatimi rokmi na ňom dodnes fungujú úplne bez problémov.

O tom, že táto technológia nielen prežíva, ale sa aj naďalej aktívne udržiava a modernizuje (napríklad prispôsobovaním sa novým grafickým rozhraniam ako Wayland pre Linux alebo Metal pre macOS), hovorí aj Phil Rice vo svojej prednáške The JDK Client Desktop: 2026 and Still Swinging. Jedným z najlepších a najznámejších dôkazov životaschopnosti tejto knižnice je aj populárne vývojové prostredie IntelliJ IDEA od JetBrains, ktoré už štvrťstoročie úspešne stojí práve na Swingu. V ďalšej verzii JDK dokonca pribudnú dva nové komponenty, JDatePicker a  JCalendarPane.
JDatePicker https://bugs.openjdk.org/browse/JDK-8379439
--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# and bit of available features in JDK 28

Project Valhalla

Project Valhalla is augmenting the Java object model with value objects, combining the abstractions of object-oriented programming with the performance characteristics of simple primitives.

This Project is sponsored by the HotSpot Group.
What’s New?

August 2026:

    JEP 401: Value Objects (Preview) and JEP 539: Strict Field Initialization in the JVM (Preview) are now integrated and will be included in JDK 28! Try out value objects today with an early-access JDK 28 build.

=>
401:	Value Objects (Preview)
535:	Shenandoah GC: Generational Mode by Default
539:	Strict Field Initialization in the JVM (Preview)

..Still preview, but at least you do not need special repo/build

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 523: [Make G1 the Default Garbage Collector in All Environments](https://openjdk.org/jeps/523)

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 534: [Compact Object Headers by Default](https://openjdk.org/jeps/534) 1/2
 * Started at RH at our Cubicle
	* Why some loads are better with OpenJ9? (around JDK..18?)
 * Experimental in JDK 24 - https://openjdk.org/jeps/450
   * -XX:+UnlockExperimentalVMOptions -XX:+UseCompactObjectHeaders
 * Stable in JDK 25 - https://openjdk.org/jeps/519
   * -XX:+UseCompactObjectHeaders
  * Default in 27 - https://openjdk.org/jeps/534
   * -XX:-UseCompactObjectHeaders ...
  * maximal measured impact
    * 22% less heap space and 10% less CPU time.
 * average impact 15% less hap 5% of CPU
--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 534: [Compact Object Headers by Default](https://openjdk.org/jeps/534) 2/2
 * **Each Object**  have an header.
    * Garbage collection — Storing forwarding pointers and tracking object ages;
    * Type system — Identifying an object's class, which is used for method invocation, reflection, type checks, etc.;
    * Locking — Storing information about associated light-weight and heavy-weight locks; and
    * Hash codes — Storing an object's stable identity hash code, once computed.
    * The current object header layout is split into a mark word and a class word. The mark word comes first, has the size of a machine address, and contains
  * And java application is a lot of short living small objects
  * Saving anything on each object would speed up everything from ram consumption to memory operations => performance
 * it was 98-128 bits in 64b JVM
  * Now it is 64b (and at least 4 spared for future.. for Valhalla)
```
For compact object headers, we remove the division between the mark and class words by subsuming the class pointer, in compressed form, into the mark word:
Header (compact):
64                    42                             11   7   3  0
 [CCCCCCCCCCCCCCCCCCCCCCHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHVVVVAAAASTT]
 (Compressed Class Pointer)       (Hash Code)         /(GC Age)^(Tag)
                              (Valhalla-reserved bits) (Self Forwarded Tag)
```
 * Implement 32-bit object headers —  would likely involve implementing on-demand side storage for identity hash codes -  That is our ultimate goal.
   * There may be soft limit of 4e6 class definitions
 * Demo!
 * ps:
   * 4 bits for Valhalla are publicly not clear - nullable? Synchronization?
   * Valhalla's "objects" do not have classical reference, instead are accessed via table-like (simplified) access to flattened memory
--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 1/2
(launch the big one)
 * https://openjdk.org/jeps/537
 * preview since JDK 22
   * Still rolling!
 * SIMD
 * Compare JDK 25 x 27
   * no difference
 * primitives only
 * each vector operates only with vector
   * "builder" like chaining
 * demo!
--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 1/2
 * Where it is stuck?
 * heavily Intel model of vectors
  * hardly usable on arm
    * Don't forget that JIT is great competitor
  * API not clear and hard to use (recall the complex demo)
  * Babylon based extension approach is being investigated
   * https://openjdk.org/projects/babylon/
   * `LINQ` like approach
   * High level api is produced by javac and is later on the fly compiled  to non-java languages
 * **compare x64 and aarch (and maybe others)**
  * relative comaprisons should do
--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 531: [Lazy Constants (Third Preview)](https://openjdk.org/jeps/531)
 * https://openjdk.org/jeps/531
 * First preview at JDK 25, as 502 Stable Values
 * 526 Lazy Constants since JDK 26
   * note the class changes
 * Still preview in 27
 * Targeting the Holder-Class singleton idiom and friends
 * performance improvements 25<26<?27
 * Be aware of `500: Prepare to Make Final Mean Final (JDK26)`
 * It is moreover done, but its "harder" usages (like AOT or class cache), are still to be done

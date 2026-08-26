#  Java 27 - when preview is inevitable
###    JDK27 JEPs in nutshell

By Jiri Vanek from IBM's OpenJDK team.
16/9/2026, Brno - JUG meetup in https://impacthub.cz/brno/


Second STS release, year after feature full LTS JDK25

abstract (remove)
Cryptography in slow and necessary progress, stable and compatible VM improvements with big impact and 4 impressive tech-previews in (hopefully) last iterations
abstract longer (remove)
JDK 27, the second STS release after JDK 25 LTS, headlines with Compact Object Headers by default — shrinking them from 64 bits for ~15% less heap and ~5% less CPU. Other production features include Post-Quantum Key Exchange for
TLS 1.3, G1 as default GC, and JFR data redaction. Several long-running previews continue: Structured Concurrency (7th), Primitive Types in Patterns (5th), Lazy Constants (3rd), and Vector API (12th incubator).
Looking also a bit more ahead, JDK 28 brings Project Valhalla's Value Objects, promising to merge OOP abstractions with primitive performance, and probably a bit more

Following https://www.youtube.com/watch?v=ejhok_F3fHg
and https://www.youtube.com/watch?v=UnA2jRVNb3M

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
#  Short reminder of release cycle

3x sts - evey half a year
1x lts - every 2 years
replaced feature driven releases for jdk8 and older. JDK9 was 9/2017, JDK8 was 9/2014 and JDK7 7/2011
Originally the LTS was there every 3 years
There is no lts - lts serves for oracle jdk. Openjdk aligns to it
usually the STS serves as preview for next LTS, but whoat is not in last STS, may be in troubles

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
# Swing is not dead?

In May 2026 there was an rumor that Swing is still alive, and there is going to be new components of
* JDatePicker
* JCalendarPane.
* based on awesome https://www.youtube.com/watch?v=ux1IpWo3oXA

in **next release of JDK**

Both are https://bugs.openjdk.org/browse/JDK-8379439, and where swing is really still alive (Wakefield.....)
The components are not going to land in jdk27

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# and bit of available features in JDK 28
## Project Valhalla

https://openjdk.org/projects/valhalla/ :
Project Valhalla is augmenting the Java object model with value objects, combining the abstractions of object-oriented programming with the performance characteristics of simple primitives.

August 2026:
    JEP 401: Value Objects (Preview) and JEP 539: Strict Field Initialization in the JVM (Preview) are now integrated and will be included in JDK 28! Try out value objects today with an early-access JDK 28 build.

=> https://openjdk.org/projects/jdk/28/ :
** 401:	Value Objects (Preview)
   535:	Shenandoah GC: Generational Mode by Default
** 539:	Strict Field Initialization in the JVM (Preview)
   540:	Simple JSON API (Incubator)
   541:	Deprecate the macOS/x64 Port for Removal
JEPs proposed to target JDK 28	review ends
542:	PEM Encodings of Cryptographic Objects	2026/08/26 
  No more preview?

So Valhalla will still be preview, but at least you do not need special repo/build and is just ok to enable it on cmdline

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# [Post-Quantum Hybrid Key Exchange for TLS 1.3](https://openjdk.org/jeps/527)

Implementing these hybrid key exchange schemes for TLS is the next step in the platform's support of post-quantum cryptography.

blocks for implementing hybrid key exchange schemes are there:
  * the addition of the KEM API in Java 21 (JEP 452)
  * the ML-KEM algorithm in Java 24 (JEP 496).

=>  three new post-quantum hybrid key exchange schemes that combine ML-KEM with the traditional Ephemeral Elliptic-Curve Diffie-Hellman (ECDHE) algorithms:

```
params.setNamedGroups(new String[] {
    "SecP256r1MLKEM768", "X25519MLKEM768"/*, "secp256r1", "x25519"*/
});
```

Where first two are quantum, other other two are backwards compatibility

Demo!

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 536: [JFR In-Process Data Redaction](https://openjdk.org/jeps/536)

* Removal of sensitive information from JFR events and recordings
* Default behavior is changed, use ` -XX:FlightRecorderOptions:'redact-argument=none,redact-key=none' to restore it
* it is glob, not regex (speed)

Demo!

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 523: [Make G1 the Default Garbage Collector in All Environments](https://openjdk.org/jeps/523)

* It is not going to Interfere with user selection: A collector chosen explicitly will always override the JVM’s selection.
* In scenarios in which the JVM previously selected the Serial collector, the performance should not degrade significantly.
  * constrained environments were still using Serial
* It was long way since JDK9 (default collector for server environments (JEP 248))

* improved the G1 collector across all metrics
  * 522:	G1 GC: Improve Throughput by Reducing Synchronization (jdk26)
  * 475:	Late Barrier Expansion for G1  (jdk24)
  * 423:	Region Pinning for G1 (jdk22)
  * 345:	NUMA-Aware Memory Allocation for G1 (jdk14)
  * 344:	Abortable Mixed Collections for G1  (jdk12)
  * 346:	Promptly Return Unused Committed Memory from G1 (jdk12)
  * 307:	Parallel Full GC for G1 (jdk10)
* Also improved shared GC parts
  * 304: Garbage-Collector Interface (jdk10)
   * new collectors: Epsilon, Shenandoah, ZGC
   * generational ZGC and Shenandoah
   * 363: Remove the Concurrent Mark Sweep (CMS) Garbage Collector
   * many small "unnamed" changes

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
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 1/3
(launch the big one)
 * https://openjdk.org/jeps/537
 * preview since JDK 22
   * --enable-preview no longer needed
   * Still rolling!
 * SIMD
 * Compare JDK 25 x 27
   * no difference
 * primitives only
 * each vector operates only with vector
   * "builder" like chaining
 * demo!

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 2/3
 * Where it is stuck?
 * heavily Intel model of vectors
  * hardly usable on arm
    * Don't forget that JIT is great competitor
  * API not clear and hard to use (recall the complex demo)
  * Babylon based extension approach is being investigated
   * https://openjdk.org/projects/babylon/
   * `LINQ` like approach
   * High level api is produced by javac and is later on the fly compiled  to non-java languages

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3a1
## Main1.java
| aarch64-4                                   | intel64-4                                   |
| ------------------------------------------- | ------------------------------------------- |
| Lists: 3281.75ms                            | Lists: 1281.0ms                             |
| Lists(1/2iter): 5045.0ms (153%)             | Lists(1/2iter): 1462.0ms (114%)             |
| Float Arrays: 1926.0ms (58%)                | Float Arrays: 753.5ms (58%)                 |
| Primitive Arrays: 114.75ms (3%)             | Primitive Arrays: 150.0ms (11%)             |
| -------------                               | -------------                               |
| List: 2754.25ms (83%)                       | List: 939.25ms (73%)                        |
| List(1/2iter): 3037.0ms (92%)               | List(1/2iter): 1355.75ms (105%)             |
| List(iter): 2875.25ms (87%)                 | List(iter): 1061.75ms (82%)                 |
| Float Array2: 1440.75ms (43%)               | Float Array2: 656.75ms (51%)                |
| Float Array1: 968.5ms (29%)                 | Float Array1: 617.25ms (48%)                |
| Primitive Array2: 96.25ms (2%)              | Primitive Array2: 112.0ms (8%)              |
| Primitive Array1: 98.75ms (3%)              | Primitive Array1: 114.75ms (8%)             |
| vectors!                                    | vectors!                                    |
| Vectors on floats: 144.75ms (4%)            | Vectors on floats: 138.25ms (10%)           |
| Vectors on float: 120.5ms (3%)              | Vectors on float: 114.75ms (8%)             |
| Vector on float: 119.25ms (3%)              | Vector on float: 112.0ms (8%)               |
| This are not the vectors you are looing for | This are not the vectors you are looing for |
| ArchaicVector: 14167.0ms (431%)             | ArchaicVector: 9502.0ms (741%)              |
| ArchaicVectors: 13851.0ms (422%)            | ArchaicVectors: 9349.25ms (729%)            |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3a2
## Main1.java
| aarch64-16                                  | intel64-16                                  |
| ------------------------------------------- | ------------------------------------------- |
| Lists: 3044.5ms                             | Lists: 1208.75ms                            |
| Lists(1/2iter): 3159.25ms (103%)            | Lists(1/2iter): 1444.5ms (119%)             |
| Float Arrays: 1287.25ms (42%)               | Float Arrays: 736.5ms (60%)                 |
| Primitive Arrays: 130.5ms (4%)              | Primitive Arrays: 112.75ms (9%)             |
| -------------                               | -------------                               |
| List: 2777.25ms (91%)                       | List: 923.75ms (76%)                        |
| List(1/2iter): 2903.0ms (95%)               | List(1/2iter): 1321.0ms (109%)              |
| List(iter): 2800.25ms (91%)                 | List(iter): 1018.0ms (84%)                  |
| Float Array2: 1014.0ms (33%)                | Float Array2: 619.75ms (51%)                |
| Float Array1: 956.0ms (31%)                 | Float Array1: 635.5ms (52%)                 |
| Primitive Array2: 89.25ms (2%)              | Primitive Array2: 109.0ms (9%)              |
| Primitive Array1: 119.0ms (3%)              | Primitive Array1: 100.25ms (8%)             |
| vectors!                                    | vectors!                                    |
| Vectors on floats: 142.75ms (4%)            | Vectors on floats: 114.0ms (9%)             |
| Vectors on float: 113.0ms (3%)              | Vectors on float: 99.5ms (8%)               |
| Vector on float: 110.0ms (3%)               | Vector on float: 101.5ms (8%)               |
| This are not the vectors you are looing for | This are not the vectors you are looing for |
| ArchaicVector: 14547.0ms (477%)             | ArchaicVector: 9472.75ms (783%)             |
| ArchaicVectors: 15048.5ms (494%)            | ArchaicVectors: 9337.5ms (772%)             |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3b1
## Main2.java - few iterations
| aarch64-4                              | intel64-4                         |
| ----------------------------------     | --------------------------------- |
| Primitive Arrays: 106.75ms             | Primitive Arrays: 110.875ms       |
| -------------                          | -------------                     |
| Primitive Array2: 91.375ms (85%)       | Primitive Array2: 101.125ms (91%) |
| Primitive Array1: 93.375ms (87%)       | Primitive Array1: 105.75ms (95%)  |
| vectors!                               | vectors!                          |
| **Vectors on floats: 145.5ms (136%)**  | Vectors on floats: 106.25ms (95%) |
| Vectors on float: 113.125ms (105%)     | Vectors on float: 115.25ms (103%) |
| Vector on float: 113.5ms (106%)        | Vector on float: 107.375ms (96%)  |
## Main2.java - a lot of iterations
| aarch64-4                            | intel64-4                           |
| ------------------------------------ | ----------------------------------- |
| Primitive Arrays: 2369.25ms          | Primitive Arrays: 1969.5ms          |
| -------------                        | -------------                       |
| Primitive Array2: 1871.0ms (78%)     | Primitive Array2: 1623.0ms (82%)    |
| Primitive Array1: 1816.125ms (76%)   | Primitive Array1: 1509.75ms (76%)   |
| vectors!                             | vectors!                            |
| Vectors on floats: 2392.625ms (100%) | Vectors on floats: 2133.25ms (108%) |
| Vectors on float: 2209.125ms (93%)   | Vectors on float: 1803.375ms (91%)  |
| Vector on float: 1666.75ms (70%)     | Vector on float: 1670.875ms (84%)   |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3b2
## Main2.java - few iterations
| aarch64-16                             | intel64-16                         |
| -----------------------------------    | ---------------------------------- |
| Primitive Arrays: 137.625ms            | Primitive Arrays: 394.5ms          |
| -------------                          | -------------                      |
| Primitive Array2: 118.5ms (86%)        | Primitive Array2: 104.5ms (26%)    |
| Primitive Array1: 119.5ms (86%)        | Primitive Array1: 114.25ms (28%)   |
| vectors!                               | vectors!                           |
| **Vectors on floats: 172.625ms (125%)**| Vectors on floats: 119.875ms (30%) |
| **Vectors on float: 142.625ms (103%)** | Vectors on float: 153.125ms (38%)  |
| Vector on float: 144.875ms (105%)      | Vector on float: 123.25ms (31%)    |
## Main2.java - a lot of iterations
| aarch64-16                           | intel64-16                               |
| -------------------------------------| ------------------------------------     |
| Primitive Arrays: 3523.875ms         | Primitive Arrays: 2140.5ms               |
| -------------                        | -------------                            |
| Primitive Array2: 2403.625ms (68%)   | Primitive Array2: 1665.75ms (77%)        |
| Primitive Array1: 2325.875ms (66%)   | Primitive Array1: 1663.5ms (77%)         |
| vectors!                             | vectors!                                 |
| Vectors on floats: 2760.875ms (78%)  | **Vectors on floats: 2147.625ms (100%)** |
| Vectors on float: 2769.75ms (78%)    | Vectors on float: 1841.25ms (86%)        |
| Vector on float: 2033.625ms (57%)    | Vector on float: 1697.875ms (79%)        |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3c1
## Main3.java - few iterations
| aarch64-4                         | intel64-4                           |
| --------------------------------- | ----------------------------------- |
| Primitive Arrays: 139.625ms       | Primitive Arrays: 111.0ms           |
| -------------                     | -------------                       |
| Primitive Array2: 117.625ms (84%) | Primitive Array2: 105.125ms (94%)   |
| Primitive Array1: 125.625ms (89%) | Primitive Array1: 118.75ms (106%)   |
| vectors!                          | vectors!                            |
| Vectors on floats: 175.0ms (125%) | Vectors on floats: 115.375ms (103%) |
| Vectors on float: 141.5ms (101%)  | Vectors on float: 125.125ms (112%)  |
| Vector on float: 139.25ms (99%)   | Vector on float: 114.125ms (102%)   |
## Main3.java - a lot of iterations
| aarch64-4                          | intel64-4                           |
| ---------------------------------- | ----------------------------------- |
| Primitive Arrays: 2947.25ms        | Primitive Arrays: 1877.125ms        |
| -------------                      | -------------                       |
| Primitive Array2: 2624.125ms (89%) | Primitive Array2: 1537.75ms (81%)   |
| Primitive Array1: 2334.0ms (79%)   | Primitive Array1: 1511.25ms (80%)   |
| vectors!                           | vectors!                            |
| Vectors on floats: 2853.25ms (96%) | Vectors on floats: 1921.75ms (102%) |
| Vectors on float: 2446.375ms (83%) | Vectors on float: 1541.375ms (82%)  |
| Vector on float: 2285.375ms (77%)  | Vector on float: 1647.625ms (87%)   |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3c2
## Main3.java - few iterations
| aarch64-16                          | intel64-16                             |
| ----------------------------------- | ----------------------------------     |
| Primitive Arrays: 195.75ms          | Primitive Arrays: 115.625ms            |
| -------------                       | -------------                          |
| Primitive Array2: 155.5ms (79%)     | Primitive Array2: 143.75ms (124%)      |
| Primitive Array1: 149.125ms (76%)   | Primitive Array1: 108.375ms (93%)      |
| vectors!                            | vectors!                               |
| Vectors on floats: 204.875ms (104%) | Vectors on floats: 122.0ms (105%)      |
| Vectors on float: 172.625ms (88%)   | **Vectors on float: 196.625ms (170%)** |
| Vector on float: 169.375ms (86%)    | **Vector on float: 136.75ms (118%)**   |
## Main3.java - a lot of iterations
| aarch64-16                         | intel64-16                         |
| ---------------------------------- | ---------------------------------- |
| Primitive Arrays: 3263.75ms        | Primitive Arrays: 2257.0ms         |
| -------------                      | -------------                      |
| Primitive Array2: 2989.25ms (91%)  | Primitive Array2: 1687.25ms (74%)  |
| Primitive Array1: 2496.0ms (76%)   | Primitive Array1: 1719.875ms (76%) |
| vectors!                           | vectors!                           |
| Vectors on floats: 2986.25ms (91%) | Vectors on floats: 2085.75ms (92%) |
| Vectors on float: 2634.875ms (80%) | Vectors on float: 1727.875ms (76%) |
| Vector on float: 2399.875ms (73%)  | Vector on float: 1800.875ms (79%)  |

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 537: [Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537) 3/3d
## Main4.java
| aarch64-4                                | intel64-4                        |
| ------------------------------------     | -------------------------------- |
| Primitive Arrays: 3336.625ms             | Primitive Arrays: 3056.875ms     |
| **Vectors on floats: 3413.875ms (102%)** | Vectors on floats: 857.5ms (28%) |
## Main4.java
| aarch64-16                               | intel64-16                       |
| ------------------------------------     | -------------------------------- |
| Primitive Arrays: 3374.0ms               | Primitive Arrays: 3062.75ms      |
| **Vectors on floats: 3449.625ms (102%)** | Vectors on floats: 812.5ms (26%) |

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

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
# 538: [PEM Encodings of Cryptographic Objects (Third Preview)](https://openjdk.org/jeps/538)

Quite a changed api:
* The PEM class is now an ordinary class rather than a record. It now includes constructors that accept Base64-encoded content in byte arrays, which is more convenient for some use cases.
* The DEREncodable interface is now named BinaryEncodable, to more accurately describe the binary data stored in PEM text.
  * Distinguished Encoding Rules
* The EncryptedPrivateKeyInfo class now includes getKeyPair methods that decrypt PKCS#8-encoded text containing a PublicKey.
* The getKey and getKeyPair methods of EncryptedPrivateKeyInfo that took a password and Provider now take only a Key.
* The withFactory method of PEMDecoder is now named withFactoriesOf to better describe that key and certificate factories are obtained from the given Provider.
* A new CryptoException class indicates failures in cryptographic processing at runtime.

otherwise still old good RSA' "Privacy-Enhanced Mail":
```
-----BEGIN PRIVATE KEY-----
BLKAHBLAH5651BLAH
-----END PRIVATE KEY-----
```

--PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE----PAGE---
Q?

https://github.com/judovana/jdk27bjug2026

Presentation written and presented in in weak-point:

    https://github.com/tisnik/vim-weakpoint

"can you give me a summary?" ... "that is terrible summary. Give it to some AI" ....
"that looks like if AI wrote it, tell it to write it more.. literary"
  ...And that is how I got:  https://github.com/judovana/jdk27bjug2026/blob/main/poem.markdown
But you must agree ti is a good one.


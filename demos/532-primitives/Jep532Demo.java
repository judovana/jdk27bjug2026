import java.util.Map;

/**
 * JEP 532: Primitive Types in Patterns, instanceof, and switch (Fifth Preview, JDK 27)
 *
 * Before JEP 532, pattern matching only worked with reference types.  Primitives were
 * second-class citizens: you couldn't use them in instanceof patterns, couldn't switch
 * on long/float/double/boolean, and had to do manual range-checking casts.
 *
 * This JEP extends pattern matching to primitive types everywhere:
 *   - instanceof can test and safely convert between ANY primitive types
 *   - switch accepts ALL primitive types as selectors (boolean, long, float, double)
 *   - Primitive type patterns work in switch case labels (replacing default with a capture)
 *   - Record patterns support safe narrowing (double -> int only matches if exact)
 *   - "Unconditionally exact" conversion rules make exhaustiveness checking work
 *
 * Compile & run:
 *   /usr/lib/jvm/java-latest-openjdk/bin/java --enable-preview Jep532Demo.java
 */
public class Jep532Demo {

    // ── Sealed JSON model used in the record-pattern demos ──────────────

    sealed interface JsonValue permits JsonValue.JsonString, JsonValue.JsonNumber,
                                      JsonValue.JsonObject, JsonValue.JsonNull {
        record JsonString(String s)                        implements JsonValue {}
        record JsonNumber(double d)                        implements JsonValue {}
        record JsonObject(Map<String, JsonValue> map)      implements JsonValue {}
        record JsonNull()                                  implements JsonValue {}
    }

    record Status(int code) {}
    record Passenger(int yearlyFlights) {}

    static void banner(String title) {
        System.out.println();
        System.out.println("═".repeat(60));
        System.out.printf("  %s%n", title);
        System.out.println("═".repeat(60));
    }

    // ====================================================================
    // 1. Primitive type patterns in switch
    //
    // NEW: A "case int i" label catches any int value — like default, but
    // it also binds the value to a variable.  Previously, if you wanted
    // both a default branch AND the value, you had to repeat the selector
    // expression or stash it in a local.
    //
    // Guards ("when") also work on primitive patterns, so you can layer
    // range checks without nested if/else.
    // ====================================================================

    static String statusMessage(Status s) {
        return switch (s.code()) {
            case 0 -> "okay";
            case 1 -> "warning";
            case 2 -> "error";
            // NEW — "case int i" replaces "default" and captures the unmatched value
            case int i -> "unknown status: " + i;
        };
    }

    static String flightTier(Passenger p) {
        return switch (p.yearlyFlights()) {
            case 0          -> "non-flyer";
            case 1          -> "occasional";
            case 2          -> "regular (discount issued)";
            // NEW — guard on a primitive pattern: "case int i when <condition>"
            case int i when i >= 100 -> "gold card holder (" + i + " flights)";
            // Remaining ints (3..99) fall here
            case int i      -> "frequent (" + i + " flights)";
        };
    }

    static void demoPrimitivePatternsInSwitch() {
        banner("1. Primitive Type Patterns in switch");

        for (int code : new int[]{0, 1, 2, 42}) {
            System.out.printf("  status(%d) -> %s%n", code, statusMessage(new Status(code)));
        }
        System.out.println();
        for (int flights : new int[]{0, 1, 2, 50, 150}) {
            System.out.printf("  flights(%3d) -> %s%n", flights, flightTier(new Passenger(flights)));
        }
    }

    // ====================================================================
    // 2. Safe narrowing in record patterns
    //
    // NEW: When a record component is double, you can pattern-match it
    // as int (or byte, short, etc.).  The pattern only matches if the
    // conversion is EXACT — 30.0 matches int, 30.5 does not.
    //
    // Previously you had to extract the double, then manually cast:
    //   double a = ...; int age = (int)a;   // silent truncation!
    // Now the type system catches lossy conversions for you.
    // ====================================================================

    static String describeJson(JsonValue json) {
        // NEW — "JsonNumber(int age)": narrows double->int, matches only if exact
        if (json instanceof JsonValue.JsonObject(var map)
            && map.get("name") instanceof JsonValue.JsonString(String name)
            && map.get("age")  instanceof JsonValue.JsonNumber(int age)) {
            return name + " is " + age + " years old";
        }
        // Fallback: keeps the original double when narrowing to int fails
        if (json instanceof JsonValue.JsonObject(var map)
            && map.get("name") instanceof JsonValue.JsonString(String name)
            && map.get("age")  instanceof JsonValue.JsonNumber(double age)) {
            return name + " has fractional age " + age + " (narrowing to int failed)";
        }
        return "not a person record";
    }

    static void demoSafeNarrowingInRecordPatterns() {
        banner("2. Safe Narrowing in Record Patterns");

        var exact = new JsonValue.JsonObject(Map.of(
            "name", new JsonValue.JsonString("Alice"),
            "age",  new JsonValue.JsonNumber(30.0)       // 30.0 == 30 exactly
        ));
        System.out.println("  30.0 (exact int)  : " + describeJson(exact));

        var fractional = new JsonValue.JsonObject(Map.of(
            "name", new JsonValue.JsonString("Bob"),
            "age",  new JsonValue.JsonNumber(30.5)       // 30.5 != any int
        ));
        System.out.println("  30.5 (fractional) : " + describeJson(fractional));

        System.out.println("  JsonNull          : " + describeJson(new JsonValue.JsonNull()));
    }

    // ====================================================================
    // 3. Primitive type patterns in instanceof
    //
    // NEW: "x instanceof byte b" tests whether the int value x fits in a
    // byte WITHOUT information loss, and if so, binds the narrowed value.
    //
    // This replaces the old pattern of manual range checking:
    //   if (x >= -128 && x <= 127) { byte b = (byte)x; ... }
    // which is error-prone (easy to get bounds wrong) and doesn't
    // generalize to float/double precision loss.
    // ====================================================================

    static String tryNarrowToByte(int value) {
        // NEW — "value instanceof byte b": true only when value is in [-128, 127]
        if (value instanceof byte b) {
            return b + " (fits in byte)";
        }
        return value + " (does NOT fit in byte)";
    }

    static String tryNarrowToInt(double value) {
        // NEW — "value instanceof int i": true only when the double is a whole
        // number within int range (no fractional part, no overflow)
        if (value instanceof int i) {
            return i + " (exact int from double)";
        }
        return value + " (not exact as int)";
    }

    static String tryWidenToFloat(long value) {
        // NEW — even WIDENING can be tested: long->float loses precision for
        // large values.  "value instanceof float f" checks that.
        if (value instanceof float f) {
            return f + "f (exact float from long)";
        }
        return value + "L (not exact as float — precision loss)";
    }

    static void demoPrimitiveInstanceofPatterns() {
        banner("3. Primitive Type Patterns in instanceof");

        System.out.println("  int -> byte (narrowing check):");
        for (int v : new int[]{42, 127, 128, -128, -129, 1000}) {
            System.out.printf("    %5d -> %s%n", v, tryNarrowToByte(v));
        }

        System.out.println("  double -> int (narrowing check):");
        for (double v : new double[]{100.0, 100.5, -1.0, 2_147_483_647.0, 2_147_483_648.0}) {
            System.out.printf("    %20s -> %s%n", v, tryNarrowToInt(v));
        }

        System.out.println("  long -> float (widening precision check):");
        for (long v : new long[]{1000L, 16_777_216L, 16_777_217L}) {
            System.out.printf("    %12d -> %s%n", v, tryWidenToFloat(v));
        }
    }

    // ====================================================================
    // 4. Bare primitive instanceof (type test without pattern variable)
    //
    // NEW: "x instanceof float" (no variable) is now legal.  It returns
    // true/false based on whether the conversion would be exact.
    //
    // Previously "instanceof" only worked with reference types.  You
    // couldn't ask "does this int fit in a byte?" without writing the
    // range check yourself.  Now it's a one-expression test.
    //
    // This also works across boxing boundaries: Integer instanceof float
    // unboxes and then tests the primitive conversion.
    // ====================================================================

    static void demoInstanceofTypeTests() {
        banner("4. Bare Primitive instanceof (no pattern variable)");

        // Widening: always exact
        byte b = 42;
        System.out.println("  byte 42 instanceof int      : " + (b instanceof int));     // true, always

        // Narrowing: depends on value
        int i1 = 42;
        System.out.println("  int 42 instanceof byte      : " + (i1 instanceof byte));   // true, 42 fits
        int i2 = 1000;
        System.out.println("  int 1000 instanceof byte    : " + (i2 instanceof byte));   // false, too big

        // int -> float: exact only when the int is representable in 24-bit mantissa
        int i3 = 16_777_217;  // 2^24 + 1 — cannot be represented exactly as float
        System.out.println("  int 16777217 instanceof float  : " + (i3 instanceof float));  // false!
        System.out.println("  int 16777217 instanceof double : " + (i3 instanceof double)); // true, 53-bit mantissa

        // float/double -> narrower types: depends on whether value is whole & in range
        float f = 1000.0f;
        System.out.println("  float 1000.0 instanceof byte   : " + (f instanceof byte));   // false, >127
        System.out.println("  float 1000.0 instanceof int    : " + (f instanceof int));    // true, 1000 is exact

        double d = 1000.0d;
        System.out.println("  double 1000.0 instanceof float : " + (d instanceof float));  // true, exact

        // NEW — works across boxing boundaries (unbox + convert)
        Integer ii1 = 1000;
        System.out.println("  Integer 1000 instanceof float  : " + (ii1 instanceof float));  // true
        Integer ii2 = 16_777_217;
        System.out.println("  Integer 16777217 instanceof float  : " + (ii2 instanceof float));  // false
        System.out.println("  Integer 16777217 instanceof double : " + (ii2 instanceof double)); // true
    }

    // ====================================================================
    // 5. Switch on boolean, long, float, and double
    //
    // NEW: Before JEP 532, switch only accepted byte, short, char, int
    // (and their wrappers), String, and enums.  Now ALL primitive types
    // work as switch selectors.
    //
    // boolean: exhaustive with just case true + case false (no default).
    // long: supports long literals like 10_000_000_000L.
    // float/double: uses representation equivalence (IEEE 754 bit pattern).
    // ====================================================================

    static String booleanSwitch(boolean loggedIn) {
        // NEW — boolean as switch selector; two cases = exhaustive, no default needed
        return switch (loggedIn) {
            case true  -> "authenticated user";
            case false -> "anonymous guest";
        };
    }

    static String longSwitch(long value) {
        // NEW — long as switch selector; supports constants beyond int range
        return switch (value) {
            case 1L              -> "one";
            case 2L              -> "two";
            case 10_000_000_000L -> "ten billion (impossible with int switch!)";
            case 20_000_000_000L -> "twenty billion";
            case long x          -> "other: " + x;
        };
    }

    static String floatSwitch(float value) {
        // NEW — float as switch selector; literal cases use IEEE 754 bit comparison
        return switch (value) {
            case 0.0f                         -> "zero";
            case 1.0f                         -> "one";
            case Float.POSITIVE_INFINITY      -> "+infinity";
            case Float.NEGATIVE_INFINITY      -> "-infinity";
            case float x when Float.isNaN(x)  -> "NaN (must use guard — NaN != NaN)";
            case float x when x > 0           -> "positive: " + x;
            case float x                      -> "non-positive: " + x;
        };
    }

    static String doubleSwitch(double value) {
        // NEW — double as switch selector
        return switch (value) {
            case 0.0                            -> "zero";
            case Math.PI                        -> "pi";
            case Math.E                         -> "euler's number";
            case Double.POSITIVE_INFINITY       -> "+infinity";
            case double x when Double.isNaN(x)  -> "NaN";
            case double x when x > 0            -> "positive: " + x;
            case double x                       -> "non-positive: " + x;
        };
    }

    static void demoSwitchOnAllPrimitives() {
        banner("5. Switch on boolean, long, float, double");

        System.out.println("  boolean switch (exhaustive without default):");
        System.out.println("    true  -> " + booleanSwitch(true));
        System.out.println("    false -> " + booleanSwitch(false));

        System.out.println("  long switch (values beyond int range):");
        for (long v : new long[]{1L, 2L, 10_000_000_000L, 20_000_000_000L, 999L}) {
            System.out.printf("    %14d -> %s%n", v, longSwitch(v));
        }

        System.out.println("  float switch (IEEE 754 representation equivalence):");
        for (float v : new float[]{0.0f, 1.0f, 3.14f, -2.5f,
                                   Float.POSITIVE_INFINITY, Float.NaN}) {
            System.out.printf("    %15s -> %s%n", v, floatSwitch(v));
        }

        System.out.println("  double switch:");
        for (double v : new double[]{0.0, Math.PI, Math.E, 42.0,
                                     Double.POSITIVE_INFINITY, Double.NaN}) {
            System.out.printf("    %20s -> %s%n", v, doubleSwitch(v));
        }
    }

    // ====================================================================
    // 6. Unconditionally exact conversions
    //
    // NEW concept: a conversion is "unconditionally exact" if it NEVER
    // loses information regardless of value.  Examples:
    //   byte -> int     (always exact — wider integral type)
    //   int  -> double  (always exact — 53-bit mantissa holds 32-bit int)
    //   int  -> float   (NOT unconditionally exact — 24-bit mantissa)
    //   float -> double (always exact — wider mantissa and exponent)
    //
    // This matters because the compiler uses unconditional exactness to
    // determine switch exhaustiveness and pattern dominance.
    // ====================================================================

    static void demoUnconditionallyExactConversions() {
        banner("6. Unconditionally Exact Conversions");

        System.out.println("  Widening integral (always exact, any value):");
        byte b = 100;
        System.out.println("    byte -> short : " + (b instanceof short));   // always true
        System.out.println("    byte -> int   : " + (b instanceof int));     // always true
        System.out.println("    byte -> long  : " + (b instanceof long));    // always true

        System.out.println("  int -> double (always exact — 53-bit mantissa > 32-bit int):");
        int imax = Integer.MAX_VALUE;
        System.out.println("    int MAX_VALUE instanceof double : " + (imax instanceof double));

        System.out.println("  int -> float (NOT always exact — 24-bit mantissa < 32-bit int):");
        int exact   = 16_777_216;   // 2^24 — largest consecutive int exact in float
        int inexact = 16_777_217;   // 2^24+1 — first int that loses precision in float
        System.out.println("    16_777_216 instanceof float : " + (exact instanceof float));    // true
        System.out.println("    16_777_217 instanceof float : " + (inexact instanceof float));  // false!

        System.out.println("  float -> double (always exact):");
        float fmax = Float.MAX_VALUE;
        System.out.println("    float MAX_VALUE instanceof double : " + (fmax instanceof double));

        System.out.println("  Boxing is always exact (int -> Integer):");
        int i4 = 42;
        System.out.println("    int 42 instanceof Integer : " + (i4 instanceof Integer));
    }

    // ====================================================================
    // 7. Exhaustiveness checking with wrapper types
    //
    // NEW rule: if you switch on a wrapper type (Byte, Integer, etc.),
    // a primitive pattern that is "unconditionally exact" on the wrapped
    // primitive type makes the switch exhaustive.
    //
    //   switch (Byte b) { case int p -> ... }   // exhaustive!
    //     because int is unconditionally exact on byte.
    //     null becomes the "remainder" and throws MatchException.
    //
    // Previously you'd need a default clause or couldn't switch on
    // wrappers with primitive patterns at all.
    // ====================================================================

    static int exhaustiveWrapperSwitch(Byte b) {
        // NEW — "case int p" exhausts Byte because int is unconditionally exact on byte.
        // No default needed.  null throws NullPointerException during unboxing.
        return switch (b) {
            case int p -> p * 2;
        };
    }

    static String exhaustiveIntegerSwitch(Integer ii) {
        // NEW — "case long l" exhausts Integer because long is unconditionally exact on int.
        return switch (ii) {
            case long l -> "long value: " + l;
        };
    }

    static void demoExhaustivenessChecking() {
        banner("7. Exhaustiveness: Wrapper Types + Primitive Patterns");

        System.out.println("  switch(Byte) with 'case int p' — no default needed:");
        System.out.println("    Byte(42)  -> " + exhaustiveWrapperSwitch((byte) 42));
        System.out.println("    Byte(-1)  -> " + exhaustiveWrapperSwitch((byte) -1));

        System.out.println("  switch(Integer) with 'case long l' — no default needed:");
        System.out.println("    Integer(999) -> " + exhaustiveIntegerSwitch(999));

        // null causes NullPointerException during unboxing (Byte -> byte)
        System.out.print("  Byte(null) -> ");
        try {
            exhaustiveWrapperSwitch(null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException (null unboxing before match)");
        }
    }

    // ====================================================================
    // 8. Dominance checking (compile-time safety)
    //
    // NEW: the compiler uses unconditionally-exact conversion rules to
    // detect unreachable case labels.  A broader pattern "dominates" a
    // more specific one, so putting the broad one first is a compile error.
    //
    // This prevents subtle bugs where a catch-all pattern silently
    // swallows cases you intended to handle separately.
    //
    // (These are compile errors, so we show them as comments and then
    // demonstrate the CORRECT ordering.)
    // ====================================================================

    static void demoDominanceChecking() {
        banner("8. Dominance Checking (compile-time safety)");

        System.out.println("  The compiler prevents unreachable case labels.");
        System.out.println();
        System.out.println("  ILLEGAL — broader pattern before specific constant:");
        System.out.println("    switch (someInt) {");
        System.out.println("        case float f    -> {}    // catches 16_777_216");
        System.out.println("        case 16_777_216 -> {}    // COMPILE ERROR: dominated!");
        System.out.println("    }");
        System.out.println("    Reason: 16_777_216 converts unconditionally exactly to float,");
        System.out.println("    so 'case float f' already handles it.");
        System.out.println();
        System.out.println("  ILLEGAL — unconditional pattern before another:");
        System.out.println("    switch (someInt) {");
        System.out.println("        case int _   -> {}       // matches ALL ints");
        System.out.println("        case float _ -> {}       // COMPILE ERROR: unreachable");
        System.out.println("    }");

        // CORRECT ordering: constants and guarded patterns before the catch-all
        int x = 42;
        String result = switch (x) {
            case 0                    -> "zero";
            case 1                    -> "one";
            case int i when i < 0     -> "negative: " + i;
            case int i when i >= 100  -> "large: " + i;
            case int i                -> "small positive: " + i;   // catch-all last
        };
        System.out.println();
        System.out.println("  CORRECT ordering (specific before general):");
        System.out.println("    switch(42) -> " + result);
    }

    // ====================================================================
    // 9. Floating-point case label semantics
    //
    // NEW: float/double case labels use "representation equivalence" —
    // the IEEE 754 bit pattern.  Two literals that compile to the same
    // float bits are duplicates (compile error).
    //
    // Key consequences:
    //   - 1.0f and 0.999999999f are the SAME float (compile error if both used)
    //   - 0.0f and -0.0f have different bit patterns (different cases)
    //   - NaN never equals itself, so you can't match it with a case
    //     constant — use a guard instead: "case float x when Float.isNaN(x)"
    // ====================================================================

    static void demoFloatingPointSemantics() {
        banner("9. Floating-Point Case Label Semantics");

        System.out.println("  Representation equivalence — these are the SAME float:");
        float a = 1.0f;
        float b = 0.999999999f;     // rounds to 1.0f in IEEE 754 single precision
        System.out.printf("    1.0f bits:          0x%08X%n", Float.floatToRawIntBits(a));
        System.out.printf("    0.999999999f bits:  0x%08X%n", Float.floatToRawIntBits(b));
        System.out.println("    Same bits: " + (Float.floatToRawIntBits(a) == Float.floatToRawIntBits(b)));
        System.out.println("    -> using both as case labels would be a COMPILE ERROR");

        // Positive zero vs negative zero: different bit patterns = different cases
        System.out.println();
        System.out.println("  +0.0f vs -0.0f (different IEEE 754 bit patterns):");
        float posZero = 0.0f;
        float negZero = -0.0f;
        System.out.printf("    +0.0f bits: 0x%08X%n", Float.floatToRawIntBits(posZero));
        System.out.printf("    -0.0f bits: 0x%08X%n", Float.floatToRawIntBits(negZero));

        String posResult = switch (posZero) {
            case 0.0f   -> "matched +0.0f case";
            case float x -> "other: " + x;
        };
        String negResult = switch (negZero) {
            case 0.0f   -> "matched +0.0f case (0.0f == -0.0f per IEEE 754)";
            case float x -> "other: " + x;
        };
        System.out.println("    switch(+0.0f) -> " + posResult);
        System.out.println("    switch(-0.0f) -> " + negResult);

        // NaN: cannot be a case constant because NaN != NaN
        System.out.println();
        System.out.println("  NaN handling (NaN != NaN, so no case constant works):");
        float nan = Float.NaN;
        String nanResult = switch (nan) {
            case 0.0f                          -> "zero";
            case 1.0f                          -> "one";
            case float x when Float.isNaN(x)   -> "NaN (caught via guard, not constant)";
            case float x                       -> "other: " + x;
        };
        System.out.println("    switch(NaN)   -> " + nanResult);
    }

    // ====================================================================
    // Bonus: combined real-world example
    //
    // Demonstrates multiple JEP 532 features working together:
    //   - Record pattern with safe narrowing (double -> int)
    //   - Primitive instanceof inside a guard (long instanceof int)
    //   - Nested boolean switch (exhaustive, no default)
    //   - Primitive type pattern as catch-all
    // ====================================================================

    sealed interface Measurement permits Measurement.Temperature, Measurement.Distance,
                                         Measurement.Flag {
        record Temperature(double celsius)   implements Measurement {}
        record Distance(long millimeters)    implements Measurement {}
        record Flag(boolean active)          implements Measurement {}
    }

    static String describeMeasurement(Measurement m) {
        return switch (m) {
            // Guard on a record pattern
            case Measurement.Temperature(double c) when c < -273.15
                -> "invalid (below absolute zero)";
            // Safe narrowing: double -> int in record pattern (only matches whole degrees)
            case Measurement.Temperature(int c)
                -> "temperature: " + c + "°C (whole degrees)";
            // Fallback for fractional temperatures
            case Measurement.Temperature(double c)
                -> String.format("temperature: %.2f°C (fractional)", c);
            // Primitive instanceof inside a case guard: does long fit in int?
            case Measurement.Distance(long mm) when mm instanceof int i
                -> "short distance: " + i + " mm (fits in int)";
            case Measurement.Distance(long mm)
                -> "long distance: " + mm + " mm (needs long)";
            // Nested boolean switch — exhaustive with true+false, no default
            case Measurement.Flag(boolean b)
                -> switch (b) {
                       case true  -> "flag: ON";
                       case false -> "flag: OFF";
                   };
        };
    }

    static void demoCombinedExample() {
        banner("Bonus: Combined Real-World Example");

        Measurement[] readings = {
            new Measurement.Temperature(22.0),          // exact int
            new Measurement.Temperature(36.6),          // fractional
            new Measurement.Temperature(-300.0),        // below absolute zero
            new Measurement.Distance(500L),             // fits in int
            new Measurement.Distance(5_000_000_000L),   // needs long
            new Measurement.Flag(true),
            new Measurement.Flag(false),
        };

        for (Measurement m : readings) {
            System.out.println("  " + m);
            System.out.println("    -> " + describeMeasurement(m));
        }
    }

    // ====================================================================

    public static void main(String[] args) {
        System.out.println("JEP 532: Primitive Types in Patterns, instanceof, and switch");
        System.out.println("Fifth Preview — JDK 27");

        demoPrimitivePatternsInSwitch();       // 1
        demoSafeNarrowingInRecordPatterns();   // 2
        demoPrimitiveInstanceofPatterns();     // 3
        demoInstanceofTypeTests();             // 4
        demoSwitchOnAllPrimitives();           // 5
        demoUnconditionallyExactConversions(); // 6
        demoExhaustivenessChecking();          // 7
        demoDominanceChecking();               // 8
        demoFloatingPointSemantics();          // 9
        demoCombinedExample();                 // Bonus
    }
}

import java.lang.LazyConstant;

public class LazyDemo2627 {

    public static void main(String[] args) throws Exception {

        long start, end;

        // ------------------------
        // LAZY: force class load
        // ------------------------
        start = System.nanoTime();
        Class.forName("Lazy");
        end = System.nanoTime();
        System.out.println("Lazy init (no computation): " + (end - start)/1_000_000 + " ms");


        // ------------------------
        // EAGER: force class init
        // ------------------------
        start = System.nanoTime();
        Class.forName("Eager");
        end = System.nanoTime();
        System.out.println("Eager init (All computation): " + (end - start)/1_000_000 + " ms");

        // ------------------------
        // LAZY IDIOM: force class init
        // ------------------------
        start = System.nanoTime();
        Class.forName("LazyIdiom");
        end = System.nanoTime();
        System.out.println("LazyIdiom init (no computation)): " + (end - start)/1_000_000 + " ms");

        // ------------------------
        // Access LazyConstant constants
        // ------------------------
        start = System.nanoTime();
        long value1 = Lazy.V1.get();
        long value2 = Lazy.V2.get();
        long value3 = Lazy.V3.get();
//        long value4 = Lazy.V4.get();
//        long value5 = Lazy.V5.get();
//        long value6 = Lazy.V6.get();
//        long value7 = Lazy.V7.get();
//        long value8 = Lazy.V8.get();
//        long value9 = Lazy.V9.get();
//        long value10 = Lazy.V10.get();
        end = System.nanoTime();
        System.out.println("LazyConstant first access (N constants): " + (end - start)/1_000_000 + " ms");
        System.out.println("Value: " + value1); // prevent optimization
        System.out.println("Value: " + value2); // prevent optimization
        System.out.println("Value: " + value3); // prevent optimization
//        System.out.println("Value: " + value4); // prevent optimization
//        System.out.println("Value: " + value5); // prevent optimization
//        System.out.println("Value: " + value6); // prevent optimization
//        System.out.println("Value: " + value7); // prevent optimization
//        System.out.println("Value: " + value8); // prevent optimization
//        System.out.println("Value: " + value9); // prevent optimization
//        System.out.println("Value: " + value10); // prevent optimization

        // ------------------------
        // Access Lazy Idiom constants
        // ------------------------
        start = System.nanoTime();
        value1 = LazyIdiom.V1();
        value2 = LazyIdiom.V2();
//        value3 = LazyIdiom.V3();
//        value4 = LazyIdiom.V4();
//        value5 = LazyIdiom.V5();
//        value6 = LazyIdiom.V6();
//        value7 = LazyIdiom.V7();
//        value8 = LazyIdiom.V8();
//        value9 = LazyIdiom.V9();
//        value10 = LazyIdiom.V10t();
        end = System.nanoTime();
        System.out.println("Lazy Idiom access (N constants): " + (end - start)/1_000_000 + " ms");
        System.out.println("Value: " + value1); // prevent optimization
        System.out.println("Value: " + value2); // prevent optimization
//        System.out.println("Value: " + value3); // prevent optimization
//        System.out.println("Value: " + value4); // prevent optimization
//        System.out.println("Value: " + value5); // prevent optimization
//        System.out.println("Value: " + value6); // prevent optimization
//        System.out.println("Value: " + value7); // prevent optimization
//        System.out.println("Value: " + value8); // prevent optimization
//        System.out.println("Value: " + value9); // prevent optimization
//        System.out.println("Value: " + value10); // prevent optimization
    }
}

class Lazy {

    static final LazyConstant<Long> V1  = LazyConstant.of(() -> Heavy.compute(1));
    static final LazyConstant<Long> V2  = LazyConstant.of(() -> Heavy.compute(2));
    static final LazyConstant<Long> V3  = LazyConstant.of(() -> Heavy.compute(3));
    static final LazyConstant<Long> V4  = LazyConstant.of(() -> Heavy.compute(4));
    static final LazyConstant<Long> V5  = LazyConstant.of(() -> Heavy.compute(5));
    static final LazyConstant<Long> V6  = LazyConstant.of(() -> Heavy.compute(6));
    static final LazyConstant<Long> V7  = LazyConstant.of(() -> Heavy.compute(7));
    static final LazyConstant<Long> V8  = LazyConstant.of(() -> Heavy.compute(8));
    static final LazyConstant<Long> V9  = LazyConstant.of(() -> Heavy.compute(9));
    static final LazyConstant<Long> V10 = LazyConstant.of(() -> Heavy.compute(10));
}

class Eager {

    static final long V1  = Heavy.compute(1);
    static final long V2  = Heavy.compute(2);
    static final long V3  = Heavy.compute(3);
    static final long V4  = Heavy.compute(4);
    static final long V5  = Heavy.compute(5);
    static final long V6  = Heavy.compute(6);
    static final long V7  = Heavy.compute(7);
    static final long V8  = Heavy.compute(8);
    static final long V9  = Heavy.compute(9);
    static final long V10 = Heavy.compute(10);
}

class LazyIdiom {

    private static class LazyHolder1 { static final long V = Heavy.compute(1);}
    public static long V1(){return LazyHolder1.V;}
    private static class LazyHolder2 { static final long V = Heavy.compute(1);}
    public static long V2(){return LazyHolder2.V;}
    private static class LazyHolder3 { static final long V = Heavy.compute(1);}
    public static long V3(){return LazyHolder3.V;}
    private static class LazyHolder4 { static final long V = Heavy.compute(1);}
    public static long V4(){return LazyHolder4.V;}
    private static class LazyHolder5 { static final long V = Heavy.compute(1);}
    public static long V5(){return LazyHolder5.V;}
    private static class LazyHolder6 { static final long V = Heavy.compute(1);}
    public static long V6(){return LazyHolder6.V;}
    private static class LazyHolder7 { static final long V = Heavy.compute(1);}
    public static long V7(){return LazyHolder7.V;}
    private static class LazyHolder8 { static final long V = Heavy.compute(1);}
    public static long V8(){return LazyHolder8.V;}
    private static class LazyHolder9 { static final long V = Heavy.compute(1);}
    public static long V9(){return LazyHolder9.V;}
    private static class LazyHolder10 { static final long V = Heavy.compute(1);}
    public static long V10(){return LazyHolder10.V;}
}

class Heavy {

    static long compute(long id) {
        long m = 0;
        for (long i = 1; i < 200_000_000; i++) {
            m = Math.max(m, i ^ 2);
        }
        return (long) m;
    }
}

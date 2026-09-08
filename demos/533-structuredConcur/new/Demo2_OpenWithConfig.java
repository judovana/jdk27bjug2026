import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;
import java.time.Duration;

/**
 * JDK 27 feature 2: open(UnaryOperator) — configure timeout, name, and thread
 * factory while keeping the default joiner. Previously required a Joiner arg.
 */
class Demo2_OpenWithConfig {

    public static void main(String[] args) throws Exception {
        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();

        try (var scope = StructuredTaskScope.open(
                cf -> cf.withThreadFactory(factory)
                        .withTimeout(Duration.ofSeconds(2))
                        .withName("configured-scope"))) {

            var task = scope.fork(() -> {
                System.out.println(Thread.currentThread().getName()); // "worker-0"
                Thread.sleep(500);
                return "done";
            });

            scope.join();
            System.out.println("Result: " + task.get());
        }
    }
}

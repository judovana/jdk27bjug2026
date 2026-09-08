import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * JDK 27 feature 4: awaitAllSuccessfulOrThrow() replaces the removed awaitAll().
 * Unlike allSuccessfulOrThrow() which returns List<T>, this returns Void —
 * use it when you care about side effects, not return values.
 */
class Demo4_AwaitAllSuccessful {

    public static void main(String[] args) throws Exception {
        awaitSideEffects();
        System.out.println("All side effects completed successfully");
    }

    static void awaitSideEffects() throws ExecutionException, InterruptedException {
        Joiner<Object, Void, ExecutionException> joiner =
            Joiner.awaitAllSuccessfulOrThrow();

        try (var scope = StructuredTaskScope.open(joiner)) {
            scope.fork(() -> { sendEmail("alice"); return null; });
            scope.fork(() -> { sendEmail("bob");   return null; });
            scope.join(); // returns Void; throws if any subtask fails
        }
    }

    static void sendEmail(String to) throws Exception {
        Thread.sleep(100);
        System.out.println("Sent email to " + to);
    }
}

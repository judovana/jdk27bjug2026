import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.List;

/**
 * JDK 27 feature 3: No-arg allSuccessfulOrThrow() / anySuccessfulOrThrow()
 * now throw ExecutionException on failure (previously they didn't exist
 * without the Function parameter).
 */
class Demo3_ExecutionException {

    public static void main(String[] args) throws InterruptedException {
        // No-arg variant — throws ExecutionException
        try {
            List<String> results = collectAll();
            System.out.println("Results: " + results);
        } catch (ExecutionException e) {
            System.out.println("ExecutionException: " + e.getCause().getMessage());
        }
    }

    static List<String> collectAll() throws ExecutionException, InterruptedException {
        // No Function arg → R_X defaults to ExecutionException
        Joiner<String, List<String>, ExecutionException> joiner =
            Joiner.allSuccessfulOrThrow();

        try (var scope = StructuredTaskScope.open(joiner)) {
            scope.fork(() -> "ok");
            scope.fork(() -> { throw new RuntimeException("boom"); });
            return scope.join(); // throws ExecutionException wrapping "boom"
        }
    }
}

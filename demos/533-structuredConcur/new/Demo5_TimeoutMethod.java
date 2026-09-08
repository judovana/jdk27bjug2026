import java.util.concurrent.*;
import java.util.concurrent.StructuredTaskScope.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * JDK 27 feature 5: timeout() replaces onTimeout() in custom Joiners.
 * Called when the scope is cancelled by timeout — must either return
 * a fallback result or throw (wrapped with CancelledByTimeoutException as cause).
 */
class Demo5_TimeoutMethod {

    public static void main(String[] args) throws Exception {
        try {
            List<String> results = fetchWithTimeout();
            System.out.println("Results: " + results);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof CancelledByTimeoutException) {
                System.out.println("Timed out — returning partial results");
            }
        }
    }

    static List<String> fetchWithTimeout() throws ExecutionException, InterruptedException {
        // Custom joiner: collect partial results, throw on timeout
        Joiner<String, List<String>, ExecutionException> joiner = new Joiner<>() {
            private final List<String> results = new ArrayList<>();

            @Override
            public boolean onFork(Subtask<? extends String> subtask) {
                return false; // don't cancel scope on fork
            }

            @Override
            public boolean onComplete(Subtask<? extends String> subtask) {
                if (subtask.state() == Subtask.State.SUCCESS) {
                    synchronized (results) { results.add(subtask.get()); }
                }
                return false; // don't cancel scope
            }

            @Override
            public List<String> result() {
                return List.copyOf(results);
            }

            // NEW in JDK 27: replaces onTimeout()
            // throw → wrapped with CancelledByTimeoutException as cause
            // or return a fallback value instead of throwing
            @Override
            public List<String> timeout() throws ExecutionException {
                throw new ExecutionException("timed out with " + results.size() + " results",
                    new CancelledByTimeoutException());
            }
        };

        try (var scope = StructuredTaskScope.open(joiner,
                cf -> cf.withTimeout(Duration.ofMillis(150)))) {
            scope.fork(() -> { Thread.sleep(50);  return "fast"; });
            scope.fork(() -> { Thread.sleep(100); return "medium"; });
            scope.fork(() -> { Thread.sleep(500); return "slow"; }); // won't finish
            return scope.join();
        }
    }
}

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * JDK 27 feature 1: R_X type parameter — join() throws YOUR checked exception,
 * not a generic ExecutionException.
 */
class Demo1_TypedExceptions {

    public static void main(String[] args) throws Exception {
        try {
            String result = fetchFirst();
            System.out.println("Result: " + result);
        } catch (ServiceException e) {
            // catch your domain type directly — no unwrapping ExecutionException
            System.out.println("Caught typed exception: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
    }

    static String fetchFirst() throws ServiceException, InterruptedException {
        Joiner<String, String, ServiceException> joiner =
            Joiner.anySuccessfulOrThrow(
                cause -> new ServiceException("all services failed", cause));

        try (var scope = StructuredTaskScope.open(joiner)) {
            scope.fork(() -> { throw new RuntimeException("service A down"); });
            scope.fork(() -> { throw new RuntimeException("service B down"); });
            return scope.join(); // throws ServiceException, not ExecutionException
        }
    }
}

class ServiceException extends Exception {
    ServiceException(String msg, Throwable cause) { super(msg, cause); }
}

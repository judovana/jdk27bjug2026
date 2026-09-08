import java.util.concurrent.StructuredTaskScope;

record UserOrder(String user, int orderId) {}

class Demo {
    public static void main(String[] args) throws Exception {
        try (var scope = StructuredTaskScope.open()) {
            var user  = scope.fork(() -> fetchUser(42));
            var order = scope.fork(() -> fetchOrder(42));
            scope.join();
            System.out.println(new UserOrder(user.get(), order.get()));
        }
    }

      // if fetchUser fails → fetchOrder is cancelled automatically
      // scope.close() guarantees both threads are done


    static String fetchUser(int id) throws Exception {
        Thread.sleep(200);
		//throw new RuntimeException("User died");
        return "alice";
    }

    static int fetchOrder(int id) throws Exception {
        Thread.sleep(300);
          return 1001;
    }
}



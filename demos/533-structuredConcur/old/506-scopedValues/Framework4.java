import java.util.*;
import java.util.concurrent.*;

public class Framework4 {

  private static Random r = new Random();
  private static final ThreadLocal<Integer> CONTEXT = new ThreadLocal<>();

  public static class Client {
      private final int id;
      public Client(int id) {
        this.id = id;
      }

      public void work() {
        try {
          int counter = 0;
          while (counter < 10) {
            counter++;
            System.err.println("Client " +id +  " serving in context " + CONTEXT.get());
            Thread.sleep(r.nextInt(1000)+250);
          }
        } catch (Exception ex) {ex.printStackTrace();}
      }
      public void fake() {
      }
  }

  public static void main(final String... args) throws Exception {
    int clients=10;
    if (args.length > 0) {
     clients = Integer.valueOf(args[0]);
    }
    ExecutorService pool=Executors.newFixedThreadPool(clients/3);
    for(int x = 0; x < clients; x++) {
      final int xx = x;
      pool.execute(new Runnable() {
        public void run() {
          if (CONTEXT.get() == null) {          CONTEXT.set(xx);  };
          final Client client = new Client(xx);
          client.work();
          //!!threadLocal.remove();
        };}
      );
    }
  }
}

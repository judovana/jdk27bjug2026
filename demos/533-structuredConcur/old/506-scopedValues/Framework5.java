import java.util.*;

public class Framework5 {

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
            if (counter==3 && id < 100) new Thread(new Runnable() {
               public void run() {
                 final Client client = new Client(id+100){};
                 client.work();
                };}
            ).start();
            counter++;
            System.err.println("Client " +id +  " serving in context " + CONTEXT.get());
            Thread.sleep(r.nextInt(1000)+250);
            fake();
          }
        } catch (Exception ex) {ex.printStackTrace();}
      }
      public void fake() {
       //CONTEXT.set(r.nextInt(100)+1000);
      }
  }

  public static void main(final String... args) throws Exception {
    int clients=10;
    if (args.length > 0) {
     clients = Integer.valueOf(args[0]);
    }
    for(int x = 0; x < clients; x++) {
      final int xx = x;
      new Thread(new Runnable() {
        public void run() {
          if (CONTEXT.get() == null) {          CONTEXT.set(xx);  };
          final Client client = new Client(xx);
          client.work();
          //!!threadLocal.remove();
        };}
      ).start();
    }
  }
}

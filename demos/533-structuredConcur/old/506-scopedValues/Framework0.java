import java.util.*;

public class Framework0 {

  private static final Integer context=1;
  private static Random r = new Random();

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
            System.err.println("Client " +id +  " serving in context " + context);
            Thread.sleep(r.nextInt(1000)+250);
            fake();
          }
        } catch (Exception ex) {ex.printStackTrace();}
      }
      public void fake() {
        //context=r.nextInt(100)+1000;
      }
  }

  public static void main(final String... args) throws Exception {
    int clients=10;
    if (args.length > 0) {
     clients = Integer.valueOf(args[0]);
    }
    for(int x = 0; x < clients; x++) {
      final Client client = new Client(x);
      new Thread(new Runnable() {
        public void run() {
          client.work();
        };}
      ).start();
    }
  }
}

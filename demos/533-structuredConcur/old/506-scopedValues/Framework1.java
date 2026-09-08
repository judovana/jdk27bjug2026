import java.util.*;

public class Framework1 {

  private static Random r = new Random();

  public static class Client {
      private final int id;
      public Client(int id) {
        this.id = id;
      }

      public void work(Integer context) {
        try {
          int counter = 0;
          while (counter < 10) {
            counter++;
            System.err.println("Client " +id +  " serving in context " + context);
            Thread.sleep(r.nextInt(1000)+250);
            fake(context);
          }
        } catch (Exception ex) {ex.printStackTrace();}
      }
      public void fake(Integer context) {
        context=r.nextInt(100)+1000;
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
          final Integer context=xx;
          final Client client = new Client(xx);
          client.work(context);
        };}
      ).start();
    }
  }
}

import java.util.*;

public class Main {

private static class MyObject {
     int i1;
//     int i2;
  }

  public static void main(String... args) throws Exception {
    long pid=ProcessHandle.current().pid();
    if  (args.length > 0) {
      System.out.println("" + pid);
    }
    final List<MyObject> list = new ArrayList<>();
    final Thread hook = new Thread() {
        public void run() {
          System.out.println("" + list.size() + " in " + pid);
        }
    };
    Runtime.getRuntime().addShutdownHook(hook);
    try {
      while(true) {
        list.add(new MyObject());
        if (list.size() % 100 == 0) {	  hook.run(); };
        if  (args.length > 0 && list.size() == Integer.valueOf(args[0])) {
          System.out.println("paused");
          System.in.read();
        }
      }
    } catch (Error ex) {
     hook.run();
     System.out.println(ex);
    }
  }
}

import java.util.*;
public class Slave {
   private static class LazyHolder {
        static final Throwable INSTANCE = new Throwable();
    }
   public StackTraceElement[] getR(){return LazyHolder.INSTANCE.getStackTrace();}
}

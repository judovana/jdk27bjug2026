import java.util.*;
public class Slave {
   private class LazyHolder {
        static final Throwable INSTANCE = new Throwable();
    }
   public StackTraceElement[] getR(){return LazyHolder.INSTANCE.getStackTrace();}
}

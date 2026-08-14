import java.util.*;
public class Slave {
   public static final Throwable r = new Throwable() ;
   public StackTraceElement[] getR(){ return r.getStackTrace();}
}

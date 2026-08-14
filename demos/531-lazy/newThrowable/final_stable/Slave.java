import java.util.*;
public class Slave {
   public final LazyConstant<Throwable> r = LazyConstant.of(() -> new Throwable());
   public StackTraceElement[] getR(){ return r.get().getStackTrace();}
}

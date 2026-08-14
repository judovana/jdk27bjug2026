import java.util.*;
public class Slave {
   public static final LazyConstant<Random> r = LazyConstant.of(() -> new Random());
   public int getR(){ return r.get().nextInt();}
}

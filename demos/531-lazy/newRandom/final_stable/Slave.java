import java.util.*;
public class Slave {
   public final LazyConstant<Random> r = LazyConstant.of(() -> new Random());
   public int getR(){ return r.get().nextInt();}
}

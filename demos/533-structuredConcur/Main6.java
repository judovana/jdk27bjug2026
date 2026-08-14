import java.util.concurrent.*;

public class Main6 {

  private static final ScopedValue<Integer> CONTEXT = ScopedValue.newInstance();

  public static void main(String... args) throws Exception {
  int xx=1;
  try (var scope1 = StructuredTaskScope.open()) {
        StructuredTaskScope.Subtask<String> t1 = scope1.fork(() ->{
            ScopedValue.where(CONTEXT, xx).run(() -> {
            try{
                try (var scope2 = StructuredTaskScope.open()) {
                    StructuredTaskScope.Subtask<String> t2 = scope2.fork(() ->{
                          try{
                              for(int x=0;x<=10; x++){
                                  System.out.println("context "+ CONTEXT.get() + " for t2. t2 lives"+x);
                                  Thread.sleep(100);
                              }
                          }catch(Exception ex) {
                              ex.printStackTrace();
                          }
                    System.out.println("t2 over");
                    });
                scope2.join();
                }
            }catch(Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("t1 over");
            });
        });
        scope1.join();
  }
  System.out.println("Main over");
}
}

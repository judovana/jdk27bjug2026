import java.util.concurrent.*;

public class Main3 {

  public static void main(String... args) throws Exception {
 Thread t3;
  try (var scope = StructuredTaskScope.open()) {

        StructuredTaskScope.Subtask<String> t1 = scope.fork(() ->{
		try{
			for(int x=0;x<=10; x++){
				System.out.println("t1 lives"+x);
				Thread.sleep(100);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("t1 over");
    });

    //scope.join();

    StructuredTaskScope.Subtask<String> t2 = scope.fork(() ->{
		try{
			for(int x=0;x<=10; x++){
				System.out.println("t2 lives"+x);
				Thread.sleep(100);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("t2 over");
    });

    //scope.join();
	t3=new Thread( ()->{
		try{
			for(int x=0;x<=10; x++){
				System.out.println("t3 lives"+x);
				Thread.sleep(100);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("t3 over");
    });
	t3.start();
    scope.join();
}
	t3.join();
	System.out.println("Main over");
  }
}

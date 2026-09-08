public class Main2 {

  public static void main(String... args) throws Exception {
	Thread t1=new Thread( ()->{
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
	t1.start();
	Thread t2=new Thread( ()->{
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
	t2.start();
	Thread t3=new Thread( ()->{
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
//	t1.join();	t2.join();	t3.join();
	System.out.println("Main over");
  }
}

public class Main4 {

  public static void main(String... args) throws Exception {
try{
	Thread t1=new Thread( ()->{
		try{
			for(int x=0;x<=10; x++){
				System.out.println("t1 lives"+x);
				Thread.sleep(100);
				if (x==3) throw new RuntimeException("bye");
			}
		}catch(Exception ex) {
			throw new RuntimeException(ex);
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
			throw new RuntimeException(ex);
		}
		System.out.println("t2 over");
    });
	t2.start();
//	t1.join();	t2.join();
} catch (Exception ex){
 ex.printStackTrace();
 System.out.println("System, failed successfully");
}
	System.out.println("Main over");
  }
}

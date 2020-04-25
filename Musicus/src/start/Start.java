package start;

public class Start implements Runnable{
	
	public static void main(String[] args) {
		Thread mainThread = new Thread(new Start());
		mainThread.start();
		
	}

	//Main Thread
	public void run() {

		
	}
	
	
	

}

public class HighVolumeObjectGenerator {
	
	Employee e6 = null;
	
	public static void main(String[] args) throws InterruptedException  {
		new HighVolumeObjectGenerator();
	}

	public HighVolumeObjectGenerator() throws InterruptedException {
		for(int i=0;i<1000;i++) {
			Employee e1 = null; //reference variable
			e1 = new Employee();	
			e1.id = 200;
			new Thread(() -> {
				try{
					hello();
				}catch(InterruptedException e){}
			}).start();
	
			e1 = null;
		}
		Thread.sleep(2000);
		System.out.println("Done!!!!!");
	}

	void hello() throws InterruptedException {
		System.out.println("Entered hello()!!!!");
	
		for(int i=0;i<200;i++) {
			Employee e2 = new Employee(); //reference variable and object creation
			Employee e3 = new Employee(); //reference variable and object creation
			new Thread(() -> {
				try {
					hi(e3);
				}catch(InterruptedException e){}
			}).start();
	
		}	
		Thread.sleep(2000);
		System.out.println("Left hello()!!!!");

	}

	void hi(Employee e) throws InterruptedException {
		System.out.println("Entered hi()!!!!");
		Employee e4 = e; //reference variable copied 
		new Thread(() -> {
			try {
				bye();
			}catch(InterruptedException ex){}
		}).start();
		Thread.sleep(3000);
		System.out.println("Left hi()!!!!");
	}

	void bye() throws InterruptedException {
		System.out.println("Entered bye()!!!!");

		for(int i=0;i<600;i++) {
			Employee e5 = null;
			e5 = new Employee(); 
			e6 = e5;
		}
		Thread.sleep(4000);
		System.out.println("Left bye()!!!!");
	}

}

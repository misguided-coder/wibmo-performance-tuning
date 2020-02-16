public class HelloWorld {
	
	static Employee e6 = null;
	
	public static void main(String[] args) throws InterruptedException  {
		Employee e1 = null; //reference variable
		e1 = new Employee();	
		e1.id = 200;
		hello();
		e1 = null;
		Thread.sleep(2000);
		System.out.println("Done!!!!!");
	}

	static void hello() throws InterruptedException {
		System.out.println("Entered hello()!!!!");
	
		Employee e2 = new Employee(); //reference variable and object creation
		Employee e3 = new Employee(); //reference variable and object creation
		hi(e3);
		Thread.sleep(2000);
		System.out.println("Left hello()!!!!");

	}

	static void hi(Employee e) throws InterruptedException {
		System.out.println("Entered hi()!!!!");
		Employee e4 = e; //reference variable copied 
		bye();
		Thread.sleep(3000);
		System.out.println("Left hi()!!!!");
	}

	static void bye() throws InterruptedException {
		System.out.println("Entered bye()!!!!");

		Employee e5 = null;
		e5 = new Employee(); 
		e6 = e5;
		Thread.sleep(4000);
		System.out.println("Left bye()!!!!");
	}

}

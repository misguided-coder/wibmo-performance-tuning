public class Main  {
	
	
	
	public Main() throws Exception {
		
		//No of users
		for(int i=0;i<20000;i++){
			Thread threadA=new Thread(new Mathmatecian(),"Mathematcian-"+i);
			threadA.start();
	
			Thread threadB=new Thread(new Shopper(),"HighShopper-"+i);
			threadB.start();	
		}

		System.out.println("Finish Line!!!");
		System.in.read();


	}
	
	public static void main(String[] args) throws Exception{
		new Main();
	}
}


class Shopper implements Runnable {

public void run(){

				ShoppingCartService shoppingCartService = new ShoppingCartService();
				OrderService orderService = new OrderService();
			
				Product p1=new Product(100,"Nano", 1000000.00,1);
				Product p2=new Product(101,"Audi", 1450000.00,1);
				Product p3=new Product(102,"BMW", 7800000.00,1);
				
				shoppingCartService.addItem(p1);
				shoppingCartService.addItem(p1);
				shoppingCartService.addItem(p1);
				shoppingCartService.addItem(p2);
				shoppingCartService.addItem(p2);
				shoppingCartService.addItem(p3);

	
				System.out.println(shoppingCartService.totalPrice());
				
				shoppingCartService.addItem(p2);
				shoppingCartService.addItem(p3);
	
				System.out.println(shoppingCartService.totalPrice());
	
				try {
					shoppingCartService.removeItem(102);
					System.out.println(shoppingCartService.totalPrice());
				} catch (ItemNotFoundException e) {
					e.printStackTrace();
				}
				
				
				System.out.println(shoppingCartService.countItems());
	
				orderService.placeOrder("ritesh",shoppingCartService.showCartDetails());
 
				shoppingCartService.emptyCart();
				
				System.out.println(shoppingCartService.countItems());

				shoppingCartService = null;
				orderService = null;

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

	}

}

class Mathmatecian implements Runnable {

	public void run(){

		CalculatorService calculatorService = new CalculatorService();

				calculatorService.doSum(10,3);
				calculatorService.doDiff(10,3);

calculatorService = null;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	}

}

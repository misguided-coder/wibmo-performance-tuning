
import java.util.Collection;

public class OrderService {

	/*
	 * placeOrder will finalize the order and will add to the database for
	 * further processing.
	 */
	public String placeOrder(String customerId,Collection<Product> products) {
		// 20 JDBC loc
		String orderID = "O"
				+ ((System.nanoTime() * Runtime.getRuntime().freeMemory()) + Runtime
						.getRuntime().availableProcessors());
		System.out.printf("Order for customer %s is placed for and Order Id : %s%n", customerId,orderID);

		return orderID;
	}

}

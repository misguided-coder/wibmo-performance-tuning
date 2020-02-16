
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/*
 * To support shopping into the cart, this
 * class will have different functionalities 
 * to be used by a shopping cart.
 * * */
public class ShoppingCartService {

	// cart variable will be holding purchased items
	Map<Long, Product> cart = null;

	public ShoppingCartService() {
		cart = new HashMap<>();
	}

	/*
	 * addItem method allows items of type Product to be added to cart.
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#addItem(com.harman.mtm.model.Product)
	 */
	public void addItem(Product product) {
		if (cart.containsKey(product.getId())) {
			Product selectedProduct = cart.get(product.getId());
			selectedProduct.setQty(selectedProduct.getQty() + 1);
		} else {
			cart.put(product.getId(), product);
		}
	}

	/*
	 * countItems will calculate and return total number of items purchased.
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#countItems()
	 */
	public int countItems() {
		int quantity = 0;
		Collection<Product> products = cart.values();
		for (Product product : products) {
			quantity = quantity + product.getQty();
		}
		return quantity;
	}

	/*
	 * removeItem method will remove entire full item from the cart.
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#removeItem(long)
	 */
	public void removeItem(long orderId) throws ItemNotFoundException {
		if (cart.containsKey(orderId)) {
			cart.remove(orderId);
		} else {
			throw new ItemNotFoundException(
					"Cart does not have product with id : " + orderId);
		}
	}

	/*
	 * totalPrice will calculate the price of all items in the cart which has to
	 * be paid in actual.
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#totalPrice()
	 */
	public double totalPrice() {
		double total = 0.0;
		final Collection<Product> products = cart.values();
		for (final Product product : products) {
			double temp = product.getPrice() * product.getQty();
			total = total + temp;
		}
		return total;
	}

	/*
	 * showCartDetails will return actual items available in the cart.
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#showCartDetails()
	 */
	public Collection<Product> showCartDetails() {
		return cart.values();
	}


	/*
	 * emptyCart will clear the cart and remove all purchages fro cart
	 */
	/* (non-Javadoc)
	 * @see com.harman.mtm.service.impl.IShoppingCasrService#emptyCart()
	 */
	public void emptyCart() {
		cart.clear();
	}

}

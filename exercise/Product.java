
public class Product {

	long id;
	String name;
	double price;
	int qty;

	public Product() {
	}

	public Product(String name, double price, int qty) {
		this.name = name;
		this.price = price;
		this.qty = qty;
	}

	public Product(long id, String name, double price, int qty) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.qty = qty;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id =id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

}

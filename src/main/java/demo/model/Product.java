package demo.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

	private static final long serialVersionUID = 5186013952828648626L;

	private String id;
	private List<String> categories;
	private String description;
	private String name;
	private String priceUsd;
	private String picture;

	public Product() {
		super();
	}
	
	public String getId() {
		return id;
	}

	public List<String> getCategories() {
		return categories;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getPriceUsd() {
		return priceUsd;
	}
	
	public String getPicture() {
		return picture;
	}

	public void setId(String productId) {
		this.id = productId;
	}

	public void setCategories(List<String> productCategory) {
		this.categories = productCategory;
	}

	public void setDescription(String productDescription) {
		this.description = productDescription;
	}

	public void setName(String productName) {
		this.name = productName;
	}

	public void setPriceUsd(String productPrice) {
		this.priceUsd = productPrice;
	}

	public void setPicture(String productImage) {
		this.picture = productImage;
	}
}
package com.siemens.einkaufsliste.database.model;

public record Product(int productID, String name, Category category, String brand, int price) {

	public enum Category {
		WHEAT, ELECTRONICS, FRUITS, VEGETABLES, MILK, DRINKS, ALCOHOL, SNACKS, HOUSEHOLD
	}

}

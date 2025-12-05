package com.siemens.einkaufsliste.database.repository;

import java.util.List;

import com.siemens.einkaufsliste.database.model.Product;

public final class ProductDatabaseRepository implements ProductRepository {

	public static final ProductDatabaseRepository REPOSITORY = new ProductDatabaseRepository();
	
	private ProductDatabaseRepository() {
		
	}
	
	@Override
	public List<Product> getProducts() {
		return null;
	}

}

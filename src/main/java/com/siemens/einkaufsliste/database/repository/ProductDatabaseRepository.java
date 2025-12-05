package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;

public final class ProductDatabaseRepository implements ProductRepository {

	public static final ProductDatabaseRepository REPOSITORY = new ProductDatabaseRepository();
	
	private ProductDatabaseRepository() {
		
	}
	
	@Override
	public List<Product> getProducts() {
		return null;
	}

	@Override
	public Optional<Product> getProduct(int productID) {
		return Optional.empty();
	}

	@Override
	public void addProduct(Product product) {
		
	}

	@Override
	public void removeProduct(int productID) {
		
	}
}

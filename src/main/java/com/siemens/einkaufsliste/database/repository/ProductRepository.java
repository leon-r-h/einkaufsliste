package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;

public interface ProductRepository {
	
	List<Product> getProducts();
	
	Optional<Product> getProduct(int productID);
	
	public Product addProduct(Product product);
	
	public void removeProduct(int productID);

}

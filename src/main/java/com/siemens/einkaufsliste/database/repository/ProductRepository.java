package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;

public interface ProductRepository {
	
	/**
	 * Retrieves all products.
	 * 
	 * @return A list of all products in the database.
	 */
	List<Product> getProducts();
	
	/**
	 * Searches for a product by ID.
	 * 
	 * @param productID The ID of the product
	 * @return An {@link Optional} containing the {@link Product}, or {@code Empty} if not found.
	 */
	Optional<Product> getProduct(int productID);
	
<<<<<<< HEAD
	public Product addProduct(Product product);
=======
	/**
	 * Adds a new product.
	 * 
	 * @param product The product to add
	 */
	public void addProduct(Product product);
>>>>>>> branch 'main' of https://github.com/leon-r-h/einkaufsliste.git
	
	/**
	 * Removes a product by ID.
	 * 
	 * @param productID The ID of the product to remove
	 */
	public void removeProduct(int productID);

}

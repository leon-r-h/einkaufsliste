package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;

public interface ProductRepository {

	List<Product> getProducts() throws DataAccessException;

	List<Product> findProducts(Product.Category searchCategory) throws DataAccessException;

	List<Product> searchProducts(String searchName) throws DataAccessException;

	List<Product> searchProducts(String searchName, int maxPrice, int minPrice, Product.Category[] categories,
			String[] brand) throws DataAccessException;

	List<String> brands() throws DataAccessException;

	Optional<Product> getProduct(int productID) throws DataAccessException;

	public Product addProduct(Product product) throws DataAccessException;

	public void removeProduct(int productID) throws DataAccessException;

}
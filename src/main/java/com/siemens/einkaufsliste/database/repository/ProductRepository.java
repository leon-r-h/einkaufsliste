package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;

/**
 * The product catalog manager.
 * <p>
 * This interface defines how we fetch available items from the database. It
 * focuses heavily on filtering, allowing the UI to {@code searchProducts} by
 * name, price range, or category without fetching the entire database.
 * </p>
 *
 * @author Georg Busche
 * @author Leon Hoffmann
 */
public interface ProductRepository {

	/**
	 * Retrieves all available products in the catalog.
	 *
	 * @return a {@link List} of all {@link Product}s
	 * @throws DataAccessException if a database error occurs
	 */
	List<Product> getProducts() throws DataAccessException;

	/**
	 * Finds all products matching a specific category.
	 *
	 * @param searchCategory the {@link Product.Category} to filter by
	 * @return a {@link List} of products in the specified category
	 * @throws DataAccessException if a database error occurs
	 */
	List<Product> findProducts(Product.Category searchCategory) throws DataAccessException;

	/**
	 * Searches for products where the name vaguely matches the search string.
	 *
	 * @param searchName the name or partial name to search for
	 * @return a {@link List} of matching products
	 * @throws DataAccessException if a database error occurs
	 */
	List<Product> searchProducts(String searchName) throws DataAccessException;

	/**
	 * Performs an advanced search for products using multiple criteria.
	 * <p>
	 * Parameters can be set to null or -1 to ignore that specific filter.
	 * </p>
	 *
	 * @param searchName the name or partial name to search for
	 * @param maxPrice   the maximum price in cents (or -1 to ignore)
	 * @param minPrice   the minimum price in cents (or -1 to ignore)
	 * @param categories an array of {@link Product.Category} to filter by (or null
	 *                   to ignore)
	 * @param brand      an array of brands to filter by (or null to ignore)
	 * @return a {@link List} of products matching all active criteria
	 * @throws DataAccessException if a database error occurs
	 */
	List<Product> searchProducts(String searchName, int maxPrice, int minPrice, Product.Category[] categories,
			String[] brand) throws DataAccessException;

	/**
	 * Retrieves a list of all unique brands currently existing in the product
	 * catalog.
	 *
	 * @return a {@link List} of brand names as Strings
	 * @throws DataAccessException if a database error occurs
	 */
	List<String> brands() throws DataAccessException;

	/**
	 * Retrieves a specific product by its unique identifier.
	 *
	 * @param productID the unique ID of the product
	 * @return an {@link Optional} containing the product if found, or
	 *         {@code Optional.empty()} if not
	 * @throws DataAccessException if a database error occurs
	 */
	Optional<Product> getProduct(int productID) throws DataAccessException;

	/**
	 * Adds a new product definition to the catalog.
	 * <p>
	 * If an identical product (same name and brand) already exists, it may return
	 * the existing one.
	 * </p>
	 *
	 * @param product the {@link Product} to add
	 * @return the saved {@link Product} with its assigned ID
	 * @throws DataAccessException if a database error occurs
	 */
	public Product addProduct(Product product) throws DataAccessException;

	/**
	 * Removes a product definition from the catalog.
	 *
	 * @param productID the ID of the product to remove
	 * @throws DataAccessException if a database error occurs
	 */
	public void removeProduct(int productID) throws DataAccessException;

}
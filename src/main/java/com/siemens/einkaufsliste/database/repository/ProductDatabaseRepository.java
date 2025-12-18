package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;

public final class ProductDatabaseRepository implements ProductRepository {

	private static final Logger LOGGER = Logger.getLogger(ProductDatabaseRepository.class.getName());

	ProductDatabaseRepository() throws DataAccessException {
		createIfNonExistent();
	}

	private void createIfNonExistent() throws DataAccessException {
		final String sql = """
				CREATE TABLE IF NOT EXISTS product (
				    productID INT AUTO_INCREMENT PRIMARY KEY,
				    name VARCHAR(255),
				    category INT,
				    brand VARCHAR(255),
				    price INT
				);
				""";
		try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public List<String> brands() throws DataAccessException {
		List<String> list = new ArrayList<>();
		final String sql = "SELECT DISTINCT brand FROM product ORDER BY brand ASC";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				String brand = resultSet.getString("brand");
				if (brand != null && !brand.isBlank()) {
					list.add(brand);
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return list;
	}

	@Override
	public List<Product> findProducts(Category searchCategory) throws DataAccessException {
		return searchProducts(null, -1, -1, new Category[] { searchCategory }, null);
	}

	@Override
	public List<Product> searchProducts(String searchName) throws DataAccessException {
		return searchProducts(searchName, -1, -1, null, null);
	}

	@Override
	public List<Product> searchProducts(String searchName, int maxPrice, int minPrice, Category[] categories,
			String[] brands) throws DataAccessException {
		final String sql = """
				SELECT * FROM product
				WHERE (? IS NULL OR (LOWER(name) LIKE ? OR SOUNDEX(name) LIKE CONCAT(TRIM(TRAILING '0' FROM SOUNDEX(?)), '%')))
				AND (? = -1 OR price <= ?)
				AND (? = -1 OR price >= ?)
				AND (? = -1 OR category = ?)
				AND (? IS NULL OR brand = ?)
				ORDER BY name ASC
				""";

		List<Product> list = new ArrayList<>();
		String rawName = (searchName != null && !searchName.isBlank()) ? searchName : null;
		String likeName = (rawName != null) ? "%" + rawName.toLowerCase() + "%" : null;
		int catVal = (categories != null && categories.length > 0) ? categories[0].ordinal() : -1;
		String brandVal = (brands != null && brands.length > 0) ? brands[0] : null;

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			if (rawName == null) {
				statement.setNull(1, Types.VARCHAR);
				statement.setNull(2, Types.VARCHAR);
				statement.setNull(3, Types.VARCHAR);
			} else {
				statement.setString(1, rawName);
				statement.setString(2, likeName);
				statement.setString(3, rawName);
			}

			statement.setInt(4, maxPrice);
			statement.setInt(5, maxPrice);
			statement.setInt(6, minPrice);
			statement.setInt(7, minPrice);
			statement.setInt(8, catVal);
			statement.setInt(9, catVal);

			if (brandVal == null) {
				statement.setNull(10, Types.VARCHAR);
				statement.setNull(11, Types.VARCHAR);
			} else {
				statement.setString(10, brandVal);
				statement.setString(11, brandVal);
			}

			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					list.add(mapToProduct(rs));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return list;
	}

	@Override
	public List<Product> getProducts() throws DataAccessException {
		return searchProducts(null, -1, -1, null, null);
	}

	@Override
	public Optional<Product> getProduct(int productID) throws DataAccessException {
		final String sql = "SELECT * FROM product WHERE productID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, productID);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToProduct(resultSet));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return Optional.empty();
	}

	private Optional<Product> getExistingProduct(String name, String brand) throws DataAccessException {
		final String sql = "SELECT * FROM product WHERE name = ? AND brand = ? LIMIT 1";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, brand);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToProduct(resultSet));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return Optional.empty();
	}

	@Override
	public Product addProduct(Product product) throws IllegalArgumentException, DataAccessException {
		Optional<Product> existing = getExistingProduct(product.name(), product.brand());
		if (existing.isPresent()) {
			return existing.get();
		}

		final String sql = "INSERT INTO product (name, category, brand, price) VALUES (?, ?, ?, ?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, product.name());
			preparedStatement.setInt(2, product.category().ordinal());
			preparedStatement.setString(3, product.brand());
			preparedStatement.setInt(4, product.price());

			if (preparedStatement.executeUpdate() == 0) {
				throw new SQLException("Creation failed, no rows affected.");
			}

			try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return new Product(generatedKeys.getInt(1), product.name(), product.category(), product.brand(),
							product.price());
				} else {
					throw new SQLException("Creation failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public void removeProduct(int productID) throws DataAccessException {
		final String sql = "DELETE FROM product WHERE productID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, productID);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	private Product mapToProduct(ResultSet resultSet) throws SQLException {
		return new Product(resultSet.getInt("productID"), resultSet.getString("name"),
				Category.values()[resultSet.getInt("category")], resultSet.getString("brand"),
				resultSet.getInt("price"));
	}
}
package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
				    category VARCHAR(50),
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
	public List<Product> searchProducts(ProductFilter filter) throws DataAccessException {
		if (filter == null || filter.isEmpty()) {
			return getProducts();
		}

		StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
		List<Object> parameters = new ArrayList<>();

		if (filter.getSearchText() != null) {
			sql.append(
					" AND (LOWER(name) LIKE ? OR SOUNDEX(name) LIKE CONCAT(TRIM(TRAILING '0' FROM SOUNDEX(?)), '%'))");
			String searchText = filter.getSearchText().toLowerCase();
			parameters.add("%" + searchText + "%");
			parameters.add(filter.getSearchText());
		}

		if (filter.getMinPrice() != null) {
			sql.append(" AND price >= ?");
			parameters.add(filter.getMinPrice());
		}
		if (filter.getMaxPrice() != null) {
			sql.append(" AND price <= ?");
			parameters.add(filter.getMaxPrice());
		}

		if (!filter.getCategories().isEmpty()) {
			sql.append(" AND category IN (");
			int i = 0;
			for (Category cat : filter.getCategories()) {
				sql.append(i == 0 ? "?" : ", ?");
				parameters.add(cat.name());
				i++;
			}
			sql.append(")");
		}

		if (!filter.getBrands().isEmpty()) {
			sql.append(" AND brand IN (");
			for (int i = 0; i < filter.getBrands().size(); i++) {
				sql.append(i == 0 ? "?" : ", ?");
				parameters.add(filter.getBrands().get(i));
			}
			sql.append(")");
		}

		sql.append(" ORDER BY name ASC");

		List<Product> results = new ArrayList<>();
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {

			for (int i = 0; i < parameters.size(); i++) {
				preparedStatement.setObject(i + 1, parameters.get(i));
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					results.add(mapToProduct(resultSet));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}

		return results;
	}

	@Override
	public List<Product> getProducts() throws DataAccessException {
		List<Product> list = new ArrayList<>();
		final String sql = "SELECT * FROM product ORDER BY name ASC";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				list.add(mapToProduct(resultSet));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return list;
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
			preparedStatement.setString(2, product.category().name());
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
				Category.valueOf(resultSet.getString("category")), resultSet.getString("brand"),
				resultSet.getInt("price"));
	}
}
package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;

// TODO: Ganze klasse aufräumen, irgendwie crazy gelöst
public final class ProductDatabaseRepository implements ProductRepository {

	ProductDatabaseRepository() {
		createIfNonExistent();
	}

	private void createIfNonExistent() {
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
			e.printStackTrace();
		}
	}

	@Override
	public List<String> brands() {
		List<String> list = new ArrayList<>();
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT DISTINCT product.brand FROM product");
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				list.add(resultSet.getString("brand"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Product> findProducts(Product.Category searchCategory) {
		return searchProducts("", -1, -1, new Category[] { searchCategory }, null);
	}

	@Override
	public List<Product> searchProducts(String searchName) {
		return searchProducts(searchName, -1, -1, null, null);
	}

	@Override
	public List<Product> searchProducts(String searchName, int maxPrice, int minPrice, Product.Category[] categorys,
			String[] brands) {
		// TODO: ??????
		// TODO: WAS Ist das für ne implementation hahahhahahahha
		// TODO: XDDDDD

		StringBuilder stringBuilder = new StringBuilder("SELECT * FROM product");

		int brandCount = (brands == null) ? 0 : brands.length;
		int categoryCount = (categorys == null) ? 0 : categorys.length;

		if (brandCount > 0 || categoryCount > 0 || maxPrice != -1 || minPrice != -1 || !searchName.equals("")) {
			stringBuilder.append(" WHERE ");
			if (brandCount > 0) {
				stringBuilder.append("product.brand IN (?");
				for (int i = 0; i < brandCount - 1; i++) {
					stringBuilder.append(", ?");
				}
				stringBuilder.append(") AND ");
			}
			if (categoryCount > 0) {
				stringBuilder.append("product.category IN (?");
				for (int i = 0; i < categoryCount - 1; i++) {
					stringBuilder.append(", ?");
				}
				stringBuilder.append(") AND ");
			}
			if (maxPrice != -1) {
				stringBuilder.append("product.price < ? AND ");
			}
			if (minPrice != -1) {
				stringBuilder.append("product.price > ? AND ");
			}

			if (!searchName.equals("")) {
				stringBuilder.append(
						"LOWER(name) LIKE ? OR SOUNDEX(name) LIKE CONCAT(TRIM(TRAILING '0' FROM SOUNDEX(?)), '%')");
			} else {
				stringBuilder.setLength(stringBuilder.length() - 5);
			}
		}

		List<Product> list = new ArrayList<>();

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {

			int pos = 1;

			if (brandCount > 0) {
				for (String brand : brands) {
					preparedStatement.setString(pos++, brand);
				}
			}
			if (categoryCount > 0) {
				for (Category cat : categorys) {
					preparedStatement.setInt(pos++, cat.ordinal());
				}
			}
			if (maxPrice != -1) {
				preparedStatement.setInt(pos++, maxPrice);
			}
			if (minPrice != -1) {
				preparedStatement.setInt(pos++, minPrice);
			}
			if (!searchName.equals("")) {
				preparedStatement.setString(pos++, "%" + searchName.toLowerCase() + "%");
				preparedStatement.setString(pos++, searchName);
			}

			try (ResultSet rs = preparedStatement.executeQuery()) {
				while (rs.next()) {
					list.add(mapToProduct(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Product> getProducts() {
		return searchProducts("", -1, -1, null, null);
	}

	@Override
	public Optional<Product> getProduct(int productID) {
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT * FROM product WHERE productID = ?")) {
			preparedStatement.setInt(1, productID);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToProduct(resultSet));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private boolean existsByNameAndBrand(String name, String brand) {
		final String sql = "SELECT COUNT(*) FROM product WHERE name = ? AND brand = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, brand);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Product addProduct(Product product) throws IllegalArgumentException {
		if (existsByNameAndBrand(product.name(), product.brand())) {
			throw new IllegalArgumentException("Das Produkt existiert bereits!");
		}

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"INSERT INTO product (name, category, brand, price) VALUES (?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, product.name());
			preparedStatement.setInt(2, product.category().ordinal());
			preparedStatement.setString(3, product.brand());
			preparedStatement.setInt(4, product.price());

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				throw new IllegalArgumentException();
			}

			try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return new Product(generatedKeys.getInt(1), product.name(), product.category(), product.brand(),
							product.price());
				} else {
					throw new IllegalArgumentException();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeProduct(int productID) {
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("DELETE FROM product WHERE productID = ?")) {
			preparedStatement.setInt(1, productID);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Product mapToProduct(ResultSet rs) throws SQLException {
		int categoryIndex = rs.getInt("category");
		Category category = (categoryIndex >= 0 && categoryIndex < Category.values().length)
				? Category.values()[categoryIndex]
				: Category.values()[0];
		return new Product(rs.getInt("productID"), rs.getString("name"), category, rs.getString("brand"),
				rs.getInt("price"));
	}
}
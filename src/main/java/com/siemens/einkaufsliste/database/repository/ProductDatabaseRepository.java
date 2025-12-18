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

		try {
			Connection connection = Database.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gibt alle vorhandenen Brands zurueck
	 */
	@Override
	public List<String> brands() {

		List<String> list = new ArrayList<>();

		try {
			PreparedStatement ps = Database.getConnection()
					.prepareStatement("SELECT DISTINCT product.brand FROM product");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String brand = rs.getString("brand");
				list.add(brand);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// Veraltet. In später nicht mehr benötigt stds. searchProducts nutzen
	@Override
	public List<Product> findProducts(Product.Category searchCategory) {

		List<Product> list = new ArrayList<>();
		try {
			PreparedStatement ps = Database.getConnection()
					.prepareStatement("SELECT * FROM product WHERE product.category =?");
			ps.setInt(1, searchCategory.ordinal());

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int productID = rs.getInt("productID");
				String name = rs.getString("name");
				int categoryIndex = rs.getInt("category");
				String brand = rs.getString("brand");
				int price = rs.getInt("price");

				Category c = Category.values()[0];
				if (categoryIndex >= 0 && categoryIndex < Category.values().length) {
					c = Category.values()[categoryIndex];
				}

				Product p = new Product(productID, name, c, brand, price);
				list.add(p);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;

	}

	// Veraltet. In später nicht mehr benötigt stds. searchProducts nutzen
	@Override
	public List<Product> searchProducts(String searchName) {
		String sql = """
				    SELECT * FROM product
				    WHERE LOWER(name) LIKE ?
				    OR SOUNDEX(name) LIKE CONCAT(TRIM(TRAILING '0' FROM SOUNDEX(?)), '%')
				""";

		List<Product> list = new ArrayList<>();
		try {
			PreparedStatement ps = Database.getConnection().prepareStatement(sql);
			ps.setString(1, "%" + searchName.toLowerCase() + "%");

			ps.setString(2, searchName);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int productID = rs.getInt("productID");
					String name = rs.getString("name");
					int categoryIndex = rs.getInt("category");
					String brand = rs.getString("brand");
					int price = rs.getInt("price");

					Category c = Category.values()[0];
					if (categoryIndex >= 0 && categoryIndex < Category.values().length) {
						c = Category.values()[categoryIndex];
					}

					list.add(new Product(productID, name, c, brand, price));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * Gibt eine Liste an Products nach gegebenen Filterkriterien zurueck.
	 * Standartwerte: (searchName: "" , maxPrice: -1, minPrice: -1, categorys: null,
	 * brands: null) => alle Products
	 *
	 */
	@Override
	public List<Product> searchProducts(String searchName, int maxPrice, int minPrice, Product.Category[] categorys,
			String[] brands) {

		StringBuilder sb = new StringBuilder("SELECT * FROM product");

		int brandCount;
		int categoryCount;

		if (brands == null) {
			brandCount = 0;
		} else {
			brandCount = brands.length;
		}

		if (categorys == null) {
			categoryCount = 0;
		} else {
			categoryCount = categorys.length;
		}

		// Anzahl der ?
		if (brandCount > 0 || categoryCount > 0 || maxPrice != -1 || minPrice != -1 || !searchName.equals("")) {
			sb.append(" WHERE ");

			if (brandCount > 0) {
				sb.append("product.brand IN (?");
				for (int i = 0; i < brandCount - 1; i++) {
					sb.append(", ?");
				}
				sb.append(") AND ");
			}

			if (categoryCount > 0) {
				sb.append("product.category IN (?");
				for (int i = 0; i < categoryCount - 1; i++) {
					sb.append(", ?");
				}
				sb.append(") AND ");
			}

			if (maxPrice != -1) {
				sb.append("product.price < ? AND ");
			}

			if (minPrice != -1) {
				sb.append("product.price > ? AND ");
			}

			if (!searchName.equals("")) {
				sb.append("LOWER(name) LIKE ? OR SOUNDEX(name) LIKE CONCAT(TRIM(TRAILING '0' FROM SOUNDEX(?)), '%')");
			} else {
				sb.setLength(sb.length() - 5);
			}
		}

		List<Product> list = new ArrayList<>();
		try {
			// System.out.println(sb.toString());
			PreparedStatement ps = Database.getConnection().prepareStatement(sb.toString());

			int pos = 1;

			if (brandCount > 0) {
				for (int i = 0; i < brandCount; i++) {
					ps.setString(pos, brands[i]);
					pos++;
				}
			}
			if (categoryCount > 0) {
				for (int i = 0; i < categoryCount; i++) {
					ps.setInt(pos, categorys[i].ordinal());
					pos++;
				}
			}
			if (maxPrice != -1) {
				ps.setInt(pos, maxPrice);
				pos++;
			}
			if (minPrice != -1) {
				ps.setInt(pos, minPrice);
				pos++;
			}
			if (!searchName.equals("")) {
				ps.setString(pos, "%" + searchName.toLowerCase() + "%");
				pos++;
				ps.setString(pos, searchName);
			}

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int productID = rs.getInt("productID");
				String name = rs.getString("name");
				int categoryIndex = rs.getInt("category");
				String brand = rs.getString("brand");
				int price = rs.getInt("price");

				Category c = Category.values()[0];
				if (categoryIndex >= 0 && categoryIndex < Category.values().length) {
					c = Category.values()[categoryIndex];
				}

				Product p = new Product(productID, name, c, brand, price);
				list.add(p);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Gibt eine Liste aller Products in der DB zurueck.
	 */
	@Override
	public List<Product> getProducts() {
		return searchProducts("", -1, -1, null, null);
		/*
		 * List<Product> list = new ArrayList<>(); try { PreparedStatement ps =
		 * Database.getConnection().prepareStatement("SELECT * FROM product"); ResultSet
		 * rs = ps.executeQuery();
		 *
		 * while (rs.next()) { int productID = rs.getInt("productID"); String name =
		 * rs.getString("name"); int categoryIndex = rs.getInt("category"); String brand
		 * = rs.getString("brand"); int price = rs.getInt("price");
		 *
		 * Category c = Category.values()[0]; if (categoryIndex >= 0 && categoryIndex <
		 * Category.values().length) { c = Category.values()[categoryIndex]; }
		 *
		 * Product p = new Product(productID, name, c, brand, price); list.add(p); }
		 *
		 * } catch (SQLException e) { e.printStackTrace(); }
		 *
		 * return list;
		 */
	}

	/**
	 * Gibt das Product mit der productID zurueck.
	 */
	@Override
	public Optional<Product> getProduct(int productID) {

		try (PreparedStatement ps = Database.getConnection()
				.prepareStatement("SELECT * FROM product WHERE productID = ?")) {

			ps.setInt(1, productID);

			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					String name = rs.getString("name");
					int category = rs.getInt("category");
					String brand = rs.getString("brand");
					int price = rs.getInt("price");
					Category c = Category.values()[category];

					Product p = new Product(productID, name, c, brand, price);
					return Optional.of(p);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	private boolean existsByNameAndBrand(String name, String brand) {
		String sql = "SELECT COUNT(*) FROM product WHERE name = ? AND brand = ?";
		try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
			ps.setString(1, name);
			ps.setString(2, brand);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Fuegt ein Product hinzu.
	 */
	@Override
	public Product addProduct(Product product) throws IllegalArgumentException {

		if (existsByNameAndBrand(product.name(), product.brand())) {
			throw new IllegalArgumentException("Das Produkt existiert bereits!");
		}

		String name = product.name();
		Category category = product.category();
		String brand = product.brand();
		int price = product.price();

		try (PreparedStatement ps = Database.getConnection().prepareStatement(
				"INSERT INTO product (name, category, brand, price) VALUES (?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name);
			ps.setInt(2, category.ordinal());
			ps.setString(3, brand);
			ps.setInt(4, price);

			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new IllegalArgumentException();
			}

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int newID = generatedKeys.getInt(1);
					// System.out.println("Produkt hinzugefügt: "+newID);

					return new Product(newID, product.name(), product.category(), product.brand(), product.price());
				} else {
					throw new IllegalArgumentException();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * entfernt das Product mit der productID.
	 */
	@Override
	public void removeProduct(int productID) {
		try {
			PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM product WHERE productID = ?");
			ps.setInt(1, productID);
			ps.executeUpdate();

			// System.out.println("Produkt geloescht: "+productID);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

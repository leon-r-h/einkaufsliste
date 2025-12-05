package com.siemens.einkaufsliste.database.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.siemens.einkaufsliste.database.Database;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;

public final class ProductDatabaseRepository implements ProductRepository {

	public static final ProductDatabaseRepository REPOSITORY = new ProductDatabaseRepository();
	
	Connection connection;
	
	
	
	private ProductDatabaseRepository() {
		 this.connection = Database.getConnection();
	}
	
	@Override
	public List<Product> getProducts() {
		
		List<Product> list = new ArrayList<>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM product");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				int productID = rs.getInt("productID");
				String name = rs.getString("name");
				String category = rs.getString("category");
				String brand = rs.getString("brand");
				int price = rs.getInt("price");
				
				Category c = Category.valueOf(category);
				
				Product p = new Product(productID, name, c, brand, price);
				list.add(p);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return list;
	}

	@Override
	public Optional<Product> getProduct(int productID) {
		return Optional.empty();
	}

	@Override
	public void addProduct(Product product) {
		/*
		int productID = product.productID();
		String name = 
		
		
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO product (productID, name, category, brand, price) VALUE ( , , , , ");
			ps.setInt(1, 0)t
			
			
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		*/
		
	}

	@Override
	public void removeProduct(int productID) {
		
	}
}

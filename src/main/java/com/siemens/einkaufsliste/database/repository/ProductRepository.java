package com.siemens.einkaufsliste.database.repository;

import java.util.List;

import com.siemens.einkaufsliste.database.model.Product;

public interface ProductRepository {
	
	List<Product> getProducts();

}

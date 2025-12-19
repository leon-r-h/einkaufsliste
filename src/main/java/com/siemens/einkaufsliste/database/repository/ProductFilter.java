package com.siemens.einkaufsliste.database.repository;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.siemens.einkaufsliste.database.model.Product;

public final class ProductFilter {

	private String searchText;
	private Set<Product.Category> categories;
	private List<String> brands;
	private Integer minPrice;
	private Integer maxPrice;

	public ProductFilter() {
		this.searchText = null;
		this.categories = EnumSet.noneOf(Product.Category.class);
		this.brands = new ArrayList<>();
		this.minPrice = null;
		this.maxPrice = null;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = (searchText != null && !searchText.isBlank()) ? searchText : null;
	}

	public Set<Product.Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Product.Category> categories) {
		this.categories = (categories != null && !categories.isEmpty()) ? EnumSet.copyOf(categories)
				: EnumSet.noneOf(Product.Category.class);
	}

	public List<String> getBrands() {
		return brands;
	}

	public void setBrands(List<String> brands) {
		this.brands = (brands != null) ? new ArrayList<>(brands) : new ArrayList<>();
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public boolean isEmpty() {
		return (searchText == null || searchText.isBlank()) && categories.isEmpty() && brands.isEmpty()
				&& minPrice == null && maxPrice == null;
	}
}
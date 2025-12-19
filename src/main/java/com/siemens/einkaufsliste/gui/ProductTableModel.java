package com.siemens.einkaufsliste.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.repository.ProductRepository;

public final class ProductTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "Name", "Category", "Brand", "Price" };

	private List<Product> products;
	private final ProductRepository productRepository;

	private String currentQuery = null;

	public ProductTableModel(ProductRepository productRepository) {
		this.productRepository = productRepository;
		this.products = new ArrayList<>();
	}

	public void search(String query) {
		this.currentQuery = query;
		final String querySnapshot = query;

		TaskQueue.submit(() -> {
			if (querySnapshot == null || querySnapshot.isBlank()) {
				return new ArrayList<>(productRepository.getProducts());
			} else {
				return new ArrayList<>(productRepository.searchProducts(querySnapshot));
			}
		}, fetched -> {
			if (!Objects.equals(currentQuery, querySnapshot)) {
				return;
			}

			products = fetched;
			fireTableDataChanged();
		});
	}

	public void reloadData() {
		search(this.currentQuery);
	}

	public Product getProductAt(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < products.size()) {
			return products.get(rowIndex);
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
		case 0 -> String.class;
		case 1 -> Product.Category.class;
		case 2 -> String.class;
		case 3 -> Integer.class;
		default -> Object.class;
		};
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= products.size()) {
			return null;
		}

		Product product = products.get(rowIndex);

		return switch (columnIndex) {
		case 0 -> product.name();
		case 1 -> product.category();
		case 2 -> product.brand();
		case 3 -> product.price();
		default -> null;
		};
	}
}

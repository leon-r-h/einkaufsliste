package com.siemens.einkaufsliste.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.siemens.einkaufsliste.database.model.Product.Category;
import com.siemens.einkaufsliste.database.repository.ProductFilter;

public final class FilterPopup extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private final Map<Category, JCheckBoxMenuItem> categoryItems = new HashMap<>();
	private final Map<String, JCheckBoxMenuItem> brandItems = new HashMap<>();
	private final Runnable onFilterChange;

	public FilterPopup(List<String> brands, Runnable onFilterChange) {
		this.onFilterChange = onFilterChange;

		JMenu categoryMenu = new JMenu("Categories");
		for (Category category : Category.values()) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(category.name());
			item.addActionListener(e -> fireFilterChange());
			categoryItems.put(category, item);
			categoryMenu.add(item);
		}
		add(categoryMenu);

		JMenu brandMenu = new JMenu("Brands");
		for (String brand : brands) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(brand);
			item.addActionListener(e -> fireFilterChange());
			brandItems.put(brand, item);
			brandMenu.add(item);
		}
		add(brandMenu);

		add(new JSeparator());

		JMenuItem clearItem = new JMenuItem("Clear Filters");
		clearItem.addActionListener(this::clearAll);
		add(clearItem);
	}

	private void clearAll(ActionEvent e) {
		categoryItems.values().forEach(item -> item.setSelected(false));
		brandItems.values().forEach(item -> item.setSelected(false));
		fireFilterChange();
	}

	private void fireFilterChange() {
		if (onFilterChange != null) {
			onFilterChange.run();
		}
	}

	public ProductFilter getFilter() {
		ProductFilter filter = new ProductFilter();

		EnumSet<Category> selectedCats = EnumSet.noneOf(Category.class);
		categoryItems.forEach((category, item) -> {
			if (item.isSelected()) {
				selectedCats.add(category);
			}
		});
		filter.setCategories(selectedCats);

		List<String> selectedBrands = new ArrayList<>();
		brandItems.forEach((brand, item) -> {
			if (item.isSelected()) {
				selectedBrands.add(brand);
			}
		});
		filter.setBrands(selectedBrands);

		return filter;
	}

}

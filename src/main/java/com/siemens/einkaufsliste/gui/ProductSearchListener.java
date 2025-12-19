package com.siemens.einkaufsliste.gui;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.siemens.einkaufsliste.database.repository.ProductFilter;

public final class ProductSearchListener implements DocumentListener {

	private final ProductTableModel model;
	private final Supplier<ProductFilter> filterSupplier;
	private final Timer debounceTimer;

	public ProductSearchListener(ProductTableModel model, Supplier<ProductFilter> filterSupplier) {
		this.model = model;
		this.filterSupplier = filterSupplier;

		this.debounceTimer = new Timer(20, (ActionEvent e) -> triggerSearch());
		this.debounceTimer.setRepeats(false);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		restartTimer();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		restartTimer();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		restartTimer();
	}

	private void restartTimer() {
		debounceTimer.restart();
	}

	private void triggerSearch() {
		ProductFilter filter = filterSupplier.get();
		model.search(filter);
	}
}

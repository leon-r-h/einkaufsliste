package com.siemens.einkaufsliste.ngui;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class ProductSearchListener implements DocumentListener {

	private final JTextField searchField;
	private final ProductTableModel model;
	private final Timer debounceTimer;

	public ProductSearchListener(JTextField searchField, ProductTableModel model) {
		this.searchField = searchField;
		this.model = model;

		this.debounceTimer = new Timer(100, (ActionEvent e) -> triggerSearch());
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
		if (debounceTimer.isRunning()) {
			debounceTimer.restart();
		} else {
			debounceTimer.start();
		}
	}

	private void triggerSearch() {
		String query = searchField.getText();
		model.search(query);
	}
}

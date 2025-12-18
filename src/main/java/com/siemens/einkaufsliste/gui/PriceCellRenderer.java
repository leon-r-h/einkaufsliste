package com.siemens.einkaufsliste.gui;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public final class PriceCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof Number) {
			double priceInEuro = ((Number) value).doubleValue() / 100.0;
			setText(currencyFormat.format(priceInEuro));
		} else {
			setText("");
		}

		setHorizontalAlignment(SwingConstants.RIGHT);

		return this;
	}

}

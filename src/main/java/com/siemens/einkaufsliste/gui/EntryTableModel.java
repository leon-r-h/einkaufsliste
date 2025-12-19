package com.siemens.einkaufsliste.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.ShoppingListItem;
import com.siemens.einkaufsliste.database.repository.EntryRepository;
import com.siemens.einkaufsliste.database.repository.ProductRepository;

public final class EntryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] COLUMN_NAMES = { "Checked", "Quantity", "Product" };

	private List<ShoppingListItem> items;
	private final EntryRepository entryRepository;
	private final UserContext userContext;

	public EntryTableModel(EntryRepository entryRepo, ProductRepository productRepo, UserContext userContext) {
		this.entryRepository = entryRepo;
		this.userContext = userContext;
		this.items = new ArrayList<>();
	}

	public void addEntry(Entry entry) {
		if (userContext.getCurrentUser().isEmpty()) {
			return;
		}

		TaskQueue.submit(() -> {
			entryRepository.addEntry(entry);
			return null;
		}, ignored -> reloadData());
	}

	public void removeEntryAt(int rowIndex) {
		if (userContext.getCurrentUser().isEmpty() || rowIndex < 0 || rowIndex >= items.size()) {
			return;
		}

		ShoppingListItem target = items.get(rowIndex);

		TaskQueue.submit(() -> {
			entryRepository.removeEntry(target.entry().entryID());
			return target.entry();
		}, deletedEntry -> {
			int idx = findIndexById(deletedEntry.entryID());
			if (idx != -1) {
				items.remove(idx);
				fireTableRowsDeleted(idx, idx);
			} else {
				reloadData();
			}
		});
	}

	public void reloadData() {
		if (userContext.getCurrentUser().isEmpty()) {
			items.clear();
			fireTableDataChanged();
			return;
		}

		int userID = userContext.getCurrentUser().get().userID();

		TaskQueue.submit(() -> entryRepository.getEntries(userID), fetchedList -> {
			items = new ArrayList<>(fetchedList);
			refreshTable();
		});
	}

	@Override
	public int getRowCount() {
		return items.size();
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
		case 0 -> Boolean.class;
		case 1 -> Integer.class;
		case 2 -> String.class;
		default -> Object.class;
		};
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= items.size()) {
			return null;
		}
		ShoppingListItem item = items.get(rowIndex);

		return switch (columnIndex) {
		case 0 -> item.entry().checkDate() != null;
		case 1 -> item.entry().quantity();
		case 2 -> item.product().name();
		default -> null;
		};
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= items.size() || userContext.getCurrentUser().isEmpty()) {
			return;
		}

		ShoppingListItem currentItem = items.get(rowIndex);
		Entry currentEntry = currentItem.entry();

		TaskQueue.submit(() -> {
			if (columnIndex == 0) {
				return ((Boolean) aValue) ? entryRepository.checkEntry(currentEntry.entryID())
						: entryRepository.uncheckEntry(currentEntry.entryID());
			} else if (columnIndex == 1) {
				return entryRepository.updateQuantity(currentEntry.entryID(), (Integer) aValue);
			}
			return null;
		}, updatedEntry -> {
			if (updatedEntry != null) {
				int idx = findIndexById(updatedEntry.entryID());
				if (idx != -1) {
					ShoppingListItem newItem = new ShoppingListItem(updatedEntry, currentItem.product());
					items.set(idx, newItem);
					refreshTable();
				} else {
					reloadData();
				}
			}
		});
	}

	private int findIndexById(int entryID) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).entry().entryID() == entryID) {
				return i;
			}
		}
		return -1;
	}

	private void refreshTable() {
		items.sort(Comparator.comparing((ShoppingListItem item) -> item.entry().checkDate() != null)
				.thenComparing(item -> item.product().name(), String::compareToIgnoreCase)
				.thenComparing(item -> item.entry().checkDate(), Comparator.nullsFirst(Comparator.naturalOrder())));
		fireTableDataChanged();
	}
}

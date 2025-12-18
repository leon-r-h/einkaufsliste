package com.siemens.einkaufsliste.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.repository.DataAccessException;
import com.siemens.einkaufsliste.database.repository.EntryRepository;
import com.siemens.einkaufsliste.database.repository.ProductRepository;

public final class EntryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(EntryTableModel.class.getName());

	private static final String[] COLUMN_NAMES = { "Checked", "Quantity", "Product" };

	private List<Entry> entries;
	private final Map<Integer, String> productCache;
	private final EntryRepository entryRepository;
	private final ProductRepository productRepository;
	private final UserContext userContext;

	public EntryTableModel(EntryRepository entryRepo, ProductRepository productRepo, UserContext userContext) {
		this.entryRepository = entryRepo;
		this.productRepository = productRepo;
		this.userContext = userContext;
		this.entries = new ArrayList<>();
		this.productCache = new HashMap<>();
	}

	public void addEntry(Entry entry) {
		if (userContext.getCurrentUser().isEmpty()) {
			return;
		}

		new SwingWorker<AddResult, Void>() {
			@Override
			protected AddResult doInBackground() throws Exception {
				Entry saved = entryRepository.addEntry(entry);
				String name = fetchProductName(saved.productID());

				return new AddResult(saved, name);
			}

			@Override
			protected void done() {
				try {
					AddResult result = get();
					entries.add(result.entry());
					productCache.put(result.entry().productID(), result.name());
					refreshTable();
				} catch (InterruptedException | ExecutionException e) {
					ErrorHandler.handle(null, e, LOGGER);
				}
			}
		}.execute();
	}

	public void removeEntryAt(int rowIndex) {
		if (userContext.getCurrentUser().isEmpty() || rowIndex < 0 || rowIndex >= entries.size()) {
			return;
		}

		Entry target = entries.get(rowIndex);

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				entryRepository.removeEntry(target.entryID());
				return null;
			}

			@Override
			protected void done() {
				try {
					get();

					if (rowIndex < entries.size() && entries.get(rowIndex).entryID() == target.entryID()) {
						entries.remove(rowIndex);
						fireTableRowsDeleted(rowIndex, rowIndex);
					} else {
						reloadData();
					}
				} catch (InterruptedException | ExecutionException e) {
					ErrorHandler.handle(null, e, LOGGER);
				}
			}
		}.execute();
	}

	public void reloadData() {
		if (userContext.getCurrentUser().isEmpty()) {
			entries.clear();
			productCache.clear();
			fireTableDataChanged();
			return;
		}

		int userID = userContext.getCurrentUser().get().userID();

		new SwingWorker<ReloadResult, Void>() {
			@Override
			protected ReloadResult doInBackground() throws Exception {
				List<Entry> freshEntries = new ArrayList<>(entryRepository.getEntries(userID));
				Map<Integer, String> freshCache = new HashMap<>();

				for (Entry e : freshEntries) {
					if (!freshCache.containsKey(e.productID())) {
						freshCache.put(e.productID(), fetchProductName(e.productID()));
					}
				}
				return new ReloadResult(freshEntries, freshCache);
			}

			@Override
			protected void done() {
				try {
					ReloadResult result = get();
					entries = result.entries();
					productCache.clear();
					productCache.putAll(result.names());
					refreshTable();
				} catch (InterruptedException | ExecutionException e) {
					ErrorHandler.handle(null, e, LOGGER);
				}
			}
		}.execute();
	}

	@Override
	public int getRowCount() {
		return entries.size();
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
		if (rowIndex >= entries.size()) {
			return null;
		}
		Entry entry = entries.get(rowIndex);
		return switch (columnIndex) {
		case 0 -> entry.checkDate() != null;
		case 1 -> entry.quantity();
		case 2 -> productCache.getOrDefault(entry.productID(), "");
		default -> null;
		};
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= entries.size() || userContext.getCurrentUser().isEmpty()) {
			return;
		}

		Entry current = entries.get(rowIndex);

		new SwingWorker<Entry, Void>() {
			@Override
			protected Entry doInBackground() throws Exception {
				if (columnIndex == 0) {
					return ((Boolean) aValue) ? entryRepository.checkEntry(current.entryID())
							: entryRepository.uncheckEntry(current.entryID());
				} else if (columnIndex == 1) {
					return entryRepository.updateQuantity(current.entryID(), (Integer) aValue);
				}

				return null;
			}

			@Override
			protected void done() {
				try {
					Entry updated = get();
					if (updated != null) {
						entries.set(rowIndex, updated);
						refreshTable();
					}
				} catch (InterruptedException | ExecutionException e) {
					ErrorHandler.handle(null, e, LOGGER);
				}
			}
		}.execute();
	}

	private void refreshTable() {
		entries.sort(Comparator.comparing((Entry e) -> e.checkDate() != null)
				.thenComparing(e -> productCache.getOrDefault(e.productID(), ""), String::compareToIgnoreCase)
				.thenComparing(Entry::checkDate, Comparator.nullsFirst(Comparator.naturalOrder())));
		fireTableDataChanged();
	}

	private String fetchProductName(int productID) throws DataAccessException {
		return productRepository.getProduct(productID).map(Product::name).orElse("");
	}

	private record AddResult(Entry entry, String name) {
	}

	private record ReloadResult(List<Entry> entries, Map<Integer, String> names) {
	}
}
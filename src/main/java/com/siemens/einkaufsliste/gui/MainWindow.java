package com.siemens.einkaufsliste.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.DataAccessException;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.EntryUtil;
import com.siemens.einkaufsliste.database.repository.ProductFilter;

public final class MainWindow implements UserContext {

	private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

	public static void main(String[] args) {
		new MainWindow();
	}

	private Optional<User> currentUser = Optional.empty();

	private MainWindow() {
		initializeLogic();
		SwingUtilities.invokeLater(this::initializeInterface);
	}

	private void initializeLogic() {
		// Set sensible logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] %4$s: %5$s%6$s%n");

		try {
			Database.connect();
		} catch (DataAccessException e) {
			ErrorHandler.handle(null, e, LOGGER);
		}
	}

	@Override
	public Optional<User> getCurrentUser() {
		return currentUser;
	}

	private static final int ROW_HEIGHT = 36;

	private JFrame frame;
	private JTable productTable;
	private ProductTableModel productModel;
	private JTextField searchField;
	private JSpinner quantitySpinner;
	private JTable shoppingListTable;
	private EntryTableModel shoppingListModel;
	private FilterPopup filterPopup;
	private ProductFilter currentFilter;

	private void initializeInterface() {
		FlatDarkLaf.setup();

		currentFilter = new ProductFilter();

		frame = new JFrame("Formula Emendi");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TaskQueue.shutdown();
				Database.disconnect();
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}

				System.exit(0);
			}
		});

		frame.setIconImages(
				FlatSVGUtils.createWindowIconImages(getClass().getResource("/com/siemens/einkaufsliste/gui/logo.svg")));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize.width / 2, screenSize.height / 2);
		frame.setLocationRelativeTo(null);

		frame.add(createToolBar(), BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createProductSearchField(),
				createShoppingListPanel());

		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.5);

		frame.add(splitPane, BorderLayout.CENTER);

		refresh();

		frame.setVisible(true);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.putClientProperty("ToolBar.floatable", false);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		DynamicAuthButton authButton = new DynamicAuthButton();
		authButton.addActionListener(e -> toggleAuth());
		toolBar.add(authButton);

		toolBar.addSeparator();

		JButton refreshButton = new JButton("Refresh");
		refreshButton.setIcon(new FlatSVGIcon(getClass().getResource("/com/siemens/einkaufsliste/gui/refresh.svg")));
		refreshButton.addActionListener(e -> refresh());
		frame.getRootPane().registerKeyboardAction(e -> refresh(), KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		toolBar.add(refreshButton);
		
		JButton exportCsvButton = new JButton("Export CSV...");
		exportCsvButton.addActionListener(e -> {
			if (currentUser.isEmpty()) {
				return;
			}

			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(frame.getTitle());
			chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));

			while (true) {
				int result = chooser.showSaveDialog(frame);
				if (result != JFileChooser.APPROVE_OPTION) {
					return;
				}

				File file = chooser.getSelectedFile();
				if (!file.getName().toLowerCase().endsWith(".csv")) {
					file = new File(file.getParentFile(), file.getName() + ".csv");
				}

				if (file.exists()) {
					continue;
				}

				try {
					EntryUtil.exportEntriesAsCsv(currentUser.get().userID(), file);
				} catch (Exception ex) {
					ErrorHandler.handle(frame, ex, LOGGER);
				}
				return;
			}
		});
		toolBar.add(exportCsvButton);

		JButton exportPdfButton = new JButton("Export PDF...");
		exportPdfButton.addActionListener(e -> {
			if (currentUser.isEmpty()) {
				return;
			}

			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(frame.getTitle());
			chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));

			while (true) {
				int result = chooser.showSaveDialog(frame);
				if (result != JFileChooser.APPROVE_OPTION) {
					return;
				}

				File file = chooser.getSelectedFile();
				if (!file.getName().toLowerCase().endsWith(".pdf")) {
					file = new File(file.getParentFile(), file.getName() + ".pdf");
				}

				if (file.exists()) {
					continue;
				}

				try {
					EntryUtil.exportEntriesAsPdf(currentUser.get().userID(), file);
				} catch (Exception ex) {
					ErrorHandler.handle(frame, ex, LOGGER);
				}
				return;
			}
		});
		toolBar.add(exportPdfButton);

		return toolBar;
	}

	private JPanel createProductSearchField() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JButton filterButton = new JButton(new FlatFilterIcon());
		filterButton.setToolTipText("Filter");
		filterButton.addActionListener(e -> {
			if (filterPopup != null) {
				filterPopup.show(filterButton, 0, filterButton.getHeight());
			}
		});

		productModel = new ProductTableModel(Database.getProducts());

		searchField = new JTextField();
		searchField.putClientProperty("JTextField.placeholderText", "Search");
		searchField.putClientProperty("JTextField.leadingIcon", new FlatSearchIcon());
		searchField.putClientProperty("JTextField.trailingComponent", filterButton);
		searchField.putClientProperty("JTextField.showClearButton", true);
		searchField.putClientProperty("JTextField.clearCallback", (Runnable) () -> searchField.setText(""));
		searchField.getDocument().addDocumentListener(new ProductSearchListener(productModel, this::getCurrentFilter));

		TaskQueue.submit(() -> Database.getProducts().brands(), brands -> {
			filterPopup = new FilterPopup(brands, this::applyFilters);
		});

		productTable = new JTable(productModel);
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productTable.setRowHeight(ROW_HEIGHT);
		productTable.setFillsViewportHeight(true);
		productTable.getColumnModel().getColumn(3).setCellRenderer(new PriceCellRenderer());
		productTable.setDragEnabled(true);

		productTable.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getSourceActions(JComponent component) {
				return COPY;
			}

			@Override
			protected Transferable createTransferable(JComponent component) {
				int rowIndex = productTable.getSelectedRow();
				if (rowIndex != -1) {
					int modelRow = productTable.convertRowIndexToModel(rowIndex);
					Product p = productModel.getProductAt(modelRow);
					if (p != null) {
						return new ProductTransferable(p);
					}
				}
				return null;
			}
		});

		JScrollPane scrollPane = new JScrollPane(productTable);

		JPanel controlPanel = new JPanel(new BorderLayout(5, 0));
		quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		JButton addToCartButton = new JButton("Add Selected");
		addToCartButton.addActionListener(e -> addSelectedProductToCart());

		controlPanel.add(quantitySpinner, BorderLayout.WEST);
		controlPanel.add(addToCartButton, BorderLayout.CENTER);

		panel.add(searchField, BorderLayout.PAGE_START);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(controlPanel, BorderLayout.PAGE_END);

		return panel;
	}

	private void applyFilters() {
		if (filterPopup != null) {
			currentFilter = filterPopup.getFilter();
			currentFilter.setSearchText(searchField.getText());
			productModel.search(currentFilter);
		}
	}

	private ProductFilter getCurrentFilter() {
		currentFilter.setSearchText(searchField.getText());
		return currentFilter;
	}

	private JPanel createShoppingListPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		DynamicHeaderLabel listTitleLabel = new DynamicHeaderLabel();
		listTitleLabel.putClientProperty("FlatLaf.styleClass", "h1");
		listTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		shoppingListModel = new EntryTableModel(Database.getEntries(), Database.getProducts(), this);

		shoppingListTable = new JTable(shoppingListModel);
		shoppingListTable.setRowHeight(ROW_HEIGHT);
		shoppingListTable.setFillsViewportHeight(true);
		shoppingListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		shoppingListTable.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canImport(TransferSupport support) {
				return currentUser.isPresent() && support.isDataFlavorSupported(ProductTransferable.PRODUCT_FLAVOR);
			}

			@Override
			public boolean importData(TransferSupport support) {
				if (!canImport(support)) {
					return false;
				}
				try {
					Product product = (Product) support.getTransferable()
							.getTransferData(ProductTransferable.PRODUCT_FLAVOR);
					addProductToEntries(product);
					return true;
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Drag and drop failed", e);

					return false;
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(shoppingListTable);

		JPanel controlPanel = new JPanel(new BorderLayout());
		JButton removeButton = new JButton("Remove Selected");
		removeButton.addActionListener(e -> removeSelectedEntry());

		controlPanel.add(removeButton, BorderLayout.CENTER);

		panel.add(listTitleLabel, BorderLayout.PAGE_START);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(controlPanel, BorderLayout.PAGE_END);

		return panel;
	}

	private void toggleAuth() {
		if (currentUser.isEmpty()) {
			Optional<User> result = LoginDialog.showLoginDialog(frame);
			if (result.isPresent()) {
				this.currentUser = result;
				refresh();
			}
		} else {
			this.currentUser = Optional.empty();
			refresh();
		}

		System.gc();
	}

	private void addSelectedProductToCart() {
		if (currentUser.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Please log in to add items.", "Login Required",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int selectedRow = productTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(frame, "Please select a product first.", frame.getTitle(),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int modelRow = productTable.convertRowIndexToModel(selectedRow);
		Product product = productModel.getProductAt(modelRow);

		if (product != null) {
			addProductToEntries(product);
		}
	}

	private void addProductToEntries(Product product) {
		if (currentUser.isEmpty()) {
			return;
		}

		int quantity = (int) quantitySpinner.getValue();
		Entry newEntry = new Entry(0, currentUser.get().userID(), product.productID(), quantity, null);
		shoppingListModel.addEntry(newEntry);
		quantitySpinner.setValue(1);
	}

	private void removeSelectedEntry() {
		if (currentUser.isEmpty()) {
			return;
		}

		int selectedRow = shoppingListTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(frame, "Please select an entry to remove.", frame.getTitle(),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int modelRow = shoppingListTable.convertRowIndexToModel(selectedRow);
		shoppingListModel.removeEntryAt(modelRow);
	}

	private void refresh() {
		productModel.reloadData();
		shoppingListModel.reloadData();

		frame.revalidate();
		frame.repaint();
	}

	private class DynamicHeaderLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		@Override
		public String getText() {
			if (currentUser == null) {
				return "Formula Emendi";
			}
			return currentUser.map(u -> u.firstName() + "'s Formula Emendi").orElse("Formula Emendi");
		}
	}

	private class DynamicAuthButton extends JButton {

		private static final long serialVersionUID = 1L;

		@Override
		public String getText() {
			invalidate(); // TODO: Dirty trick, do it better!

			return (currentUser != null && currentUser.isPresent()) ? "Sign out" : "Sign in";
		}
	}
}
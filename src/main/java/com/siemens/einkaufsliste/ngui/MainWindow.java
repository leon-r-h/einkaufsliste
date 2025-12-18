package com.siemens.einkaufsliste.ngui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.Database;

public final class MainWindow {

	public static void main(String[] args) {
		FlatDarkLaf.setup();
		new MainWindow();
	}

	private User currentUser;

	public MainWindow() {
		initializeLogic();
		SwingUtilities.invokeLater(this::initializeInterface);
	}

	private void initializeLogic() {
		Database.connect();
		currentUser = Database.getUsers().getUser(400).orElseThrow(() -> new IllegalStateException("Default user not found"));
	}

	private static final int ROW_HEIGHT = 24;

	private JFrame frame;

	private JTable productTable;
	private ProductTableModel productModel;
	private JTextField searchField;
	private JSpinner quantitySpinner;

	private JTable shoppingListTable;
	private EntryTableModel shoppingListModel;
	private JLabel listTitleLabel;

	private void initializeInterface() {
		frame = new JFrame("Formula Emendi");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize.width / 2, screenSize.height / 2);
		frame.setLocationRelativeTo(null);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createProductSearchField(),
				createShoppingListPanel());

		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(0.5);

		frame.add(splitPane);

		refresh();

		frame.setVisible(true);
	}

	private JPanel createProductSearchField() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JButton refreshButton = new JButton(new FlatSVGIcon(getClass().getResource("/com/siemens/einkaufsliste/ngui/refresh.svg")));
		refreshButton.setToolTipText("Refresh");
		refreshButton.addActionListener(e -> refresh());

		productModel = new ProductTableModel(Database.getProducts());

		searchField = new JTextField();
		searchField.putClientProperty("JTextField.placeholderText", "Search");
		searchField.putClientProperty("JTextField.leadingIcon", new FlatSearchIcon());
		searchField.putClientProperty("JTextField.trailingComponent", refreshButton);
		searchField.putClientProperty("JTextField.showClearButton", true);
		searchField.putClientProperty("JTextField.clearCallback", (Runnable)() -> searchField.setText(""));
		searchField.getDocument().addDocumentListener(new ProductSearchListener(searchField, productModel));

		productTable = new JTable(productModel);
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productTable.setRowHeight(ROW_HEIGHT);
		productTable.setFillsViewportHeight(true);

		productTable.getColumnModel().getColumn(3).setCellRenderer(new PriceCellRenderer());

		productTable.setDragEnabled(true);
		productTable.setTransferHandler(new TransferHandler() {
			@Override
			public int getSourceActions(JComponent component) {
				return COPY;
			}

			@Override
			protected Transferable createTransferable(JComponent component) {
				JTable table = (JTable) component;
				int rowIndex = table.getSelectedRow();
				if (rowIndex != -1) {
					int modelRow = table.convertRowIndexToModel(rowIndex);
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

	private JPanel createShoppingListPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		String userName = currentUser.firstName();
		listTitleLabel = new JLabel(userName + "'s Formula Emendi");
		listTitleLabel.putClientProperty("FlatLaf.styleClass", "h1");
		listTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		shoppingListModel = new EntryTableModel(Database.getEntries(), Database.getProducts(), currentUser.userID());

		shoppingListTable = new JTable(shoppingListModel);
		shoppingListTable.setRowHeight(ROW_HEIGHT);
		shoppingListTable.setFillsViewportHeight(true);
		shoppingListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		shoppingListTable.setTransferHandler(new TransferHandler() {
			@Override
			public boolean canImport(TransferSupport support) {
				return support.isDataFlavorSupported(ProductTransferable.PRODUCT_FLAVOR);
			}

			@Override
			public boolean importData(TransferSupport support) {
				if (!canImport(support)) {
					return false;
				}

				try {
					Product product = (Product) support.getTransferable().getTransferData(ProductTransferable.PRODUCT_FLAVOR);
					addProductToEntries(product);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
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

	private void addSelectedProductToCart() {
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
        int quantity = (int) quantitySpinner.getValue();

        Entry newEntry = new Entry(currentUser.userID(), product.productID(), quantity, null);
        shoppingListModel.addEntry(newEntry);

        quantitySpinner.setValue(1);
    }

	private void removeSelectedEntry() {
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
	}
}
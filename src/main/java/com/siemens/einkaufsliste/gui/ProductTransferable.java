package com.siemens.einkaufsliste.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.siemens.einkaufsliste.database.model.Product;

public final class ProductTransferable implements Transferable {

	public static final DataFlavor PRODUCT_FLAVOR = new DataFlavor(Product.class, "Product Object");

	private static final DataFlavor[] SUPPORTED_FLAVORS = { PRODUCT_FLAVOR };

	private final Product product;

	public ProductTransferable(Product product) {
		this.product = product;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return SUPPORTED_FLAVORS.clone();
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return PRODUCT_FLAVOR.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
			return product;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

}

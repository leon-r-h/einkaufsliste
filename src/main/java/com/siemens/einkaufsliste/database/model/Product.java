package com.siemens.einkaufsliste.database.model;

/**
 * The blueprint for an item that is available to be bought in the store.
 * <p>
 * This holds the static data for an item (like {@code name}, {@code brand}, and
 * {@code price}) effectively acting as the catalog definition. It is referenced
 * by an {@link Entry} when a user adds it to their list.
 * </p>
 *
 * @author Leon Hoffmann
 * @author Hakan Tosun
 * @author Georg Busche
 * @author Nils Gohr
 */
public record Product(int productID, String name, Category category, String brand, int price) {

	public enum Category {
		WHEAT, ELECTRONICS, FRUITS, VEGETABLES, MILK, DRINKS, ALCOHOL, SNACKS, HOUSEHOLD
	}

}

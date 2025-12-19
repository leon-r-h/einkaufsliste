package com.siemens.einkaufsliste.database.model;

/**
 * A composite view containing the {@link Entry} and its resolved
 * {@link Product}.
 * <p>
 * Solves the N+1 select problem by carrying the product data alongside the
 * entry.
 * </p>
 *
 * @author Leon Hoffmann
 */
public record ShoppingListItem(Entry entry, Product product) {

}

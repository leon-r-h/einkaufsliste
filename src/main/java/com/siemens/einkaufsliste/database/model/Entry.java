package com.siemens.einkaufsliste.database.model;

import java.time.LocalDate;

/**
 * Represents a single line item on a shopping list.
 * <p>
 * Architecturally, this is the link table that connects a {@link User} with a
 * specific {@link Product} they want to buy. It tracks the {@code quantity} and
 * whether the item has been "checked off" via the {@code checkDate}.
 * </p>
 *
 * @author Leon Hoffmann
 * @author Hakan Tosun
 * @author Georg Busche
 * @author Nils Gohr
 */
public record Entry(int entryID, int userID, int productID, int quantity, LocalDate checkDate) {

}

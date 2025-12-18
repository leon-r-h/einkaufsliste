package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Entry;

/**
 * The repository for managing the actual shopping list.
 * <p>
 * This interface defines how we handle the shopping list. It includes logic for
 * adding items, calculating the {@code totalPrice}, and the workflow state of
 * an item (via {@code checkEntry} and {@code uncheckEntry}).
 * </p>
 *
 * @author Hakan Tosun
 * @author Leon Hoffmann
 */
public interface EntryRepository {

	/**
	 * Deletes all entries associated with a specific user.
	 * <p>
	 * This is effectively a "Clear List" operation.
	 * </p>
	 *
	 * @param userID the ID of the user whose list should be cleared
	 * @throws DataAccessException if a database error occurs during deletion
	 */
	void nukeEntries(int userID) throws DataAccessException;

	/**
	 * Retrieves all shopping list entries for a specific user.
	 *
	 * @param userID the ID of the user
	 * @return a {@link List} of {@link Entry} objects belonging to the user
	 * @throws DataAccessException if a database error occurs while retrieving data
	 */
	List<Entry> getEntries(int userID) throws DataAccessException;

	/**
	 * Calculates the total cost of all items in the user's list.
	 *
	 * @param userID the ID of the user
	 * @return the sum of (price * quantity) for all entries, in cents
	 * @throws DataAccessException if a database error occurs during calculation
	 */
	int totalPrice(int userID) throws DataAccessException;

	/**
	 * Retrieves a specific entry by its unique identifier.
	 *
	 * @param entryID the unique ID of the entry
	 * @return an {@link Optional} containing the entry if found, or
	 *         {@code Optional.empty()} if not
	 * @throws DataAccessException if a database error occurs
	 */
	Optional<Entry> getEntry(int entryID) throws DataAccessException;

	/**
	 * Marks a specific entry as "checked" (purchased) by setting the current date
	 * as the check date.
	 *
	 * @param entryID the ID of the entry to update
	 * @return the updated {@link Entry} reflecting the new state
	 * @throws DataAccessException      if a database error occurs
	 * @throws IllegalArgumentException if the entry does not exist
	 */
	Entry checkEntry(int entryID) throws DataAccessException;

	/**
	 * Marks a specific entry as "unchecked" (pending) by clearing the check date.
	 *
	 * @param entryID the ID of the entry to update
	 * @return the updated {@link Entry} reflecting the new state
	 * @throws DataAccessException if a database error occurs or the entry cannot be
	 *                             found after update
	 */
	Entry uncheckEntry(int entryID) throws DataAccessException;

	/**
	 * Updates the quantity for a specific entry.
	 *
	 * @param entryID  the ID of the entry to update
	 * @param quantity the new quantity (must be greater than 0)
	 * @return the updated {@link Entry}
	 * @throws DataAccessException      if a database error occurs
	 * @throws IllegalArgumentException if the quantity is less than 1
	 */
	Entry updateQuantity(int entryID, int quantity) throws DataAccessException;

	/**
	 * Persists a new entry to the database.
	 *
	 * @param entry the {@link Entry} object containing the data to save
	 * @return a new {@link Entry} instance containing the generated database ID
	 * @throws DataAccessException      if a database error occurs
	 * @throws IllegalArgumentException if the entry data is invalid (e.g., quantity
	 *                                  < 1)
	 */
	Entry addEntry(Entry entry) throws DataAccessException;

	/**
	 * Permanently removes a specific entry from the database.
	 *
	 * @param entryID the ID of the entry to delete
	 * @throws DataAccessException if a database error occurs
	 */
	void removeEntry(int entryID) throws DataAccessException;
}

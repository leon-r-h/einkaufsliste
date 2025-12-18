package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Entry;

public interface EntryRepository {

	void nukeEntries(int userID) throws DataAccessException;

	/**
	 * Retrieves all entries for a specific user.
	 *
	 * @param userID The ID of the user
	 * @return A list of all entries belonging to the specified user.
	 * @throws DataAccessException
	 */
	List<Entry> getEntries(int userID) throws DataAccessException;

	int totalPrice(int userID) throws DataAccessException;

	/**
	 * Searches for an entry by ID.
	 *
	 * @param entryID The ID of the entry
	 * @return An {@link Optional} containing the {@link Entry}, or {@code Empty} if
	 *         not found.
	 * @throws DataAccessException
	 */
	Optional<Entry> getEntry(int entryID) throws DataAccessException;

	/**
	 * Marks an entry as checked.
	 *
	 * @param entryID The ID of the entry to check
	 * @throws DataAccessException
	 * @throws IllegalStateException If the entry cannot be checked (e.g., entry
	 *                               does not exist)
	 */
	Entry checkEntry(int entryID) throws DataAccessException;

	/**
	 * Marks an entry as unchecked.
	 *
	 * @param entryID The ID of the entry to uncheck
	 * @throws DataAccessException
	 * @throws IllegalStateException If the entry cannot be unchecked (e.g., entry
	 *                               does not exist)
	 */
	Entry uncheckEntry(int entryID) throws DataAccessException;

	/**
	 * Updates the quantity of an entry.
	 *
	 * @param entryID  The ID of the entry to update
	 * @param quantity The new quantity value
	 * @return Entry The entry with an updated quantity
	 * @throws DataAccessException
	 */
	Entry updateQuantity(int entryID, int quantity) throws DataAccessException;

	/**
	 * Adds a new entry.
	 *
	 * @param entry The entry to add
	 * @return The saved entry with the new, correct database ID.
	 * @throws DataAccessException
	 */
	Entry addEntry(Entry entry) throws DataAccessException;

	/**
	 * Removes an entry by ID.
	 *
	 * @param entryID The ID of the entry to remove
	 * @throws DataAccessException
	 */
	void removeEntry(int entryID) throws DataAccessException;
}

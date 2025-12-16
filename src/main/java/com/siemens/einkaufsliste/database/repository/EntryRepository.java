package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Entry;

public interface EntryRepository {

	/**
	 * Retrieves all entries for a specific user.
	 * 
	 * @param userID The ID of the user
	 * @return A list of all entries belonging to the specified user.
	 */
	List<Entry> getEntries(int userID);
	
	int totalPrice(int userID);

	/**
	 * Searches for an entry by ID.
	 * 
	 * @param entryID The ID of the entry
	 * @return An {@link Optional} containing the {@link Entry}, or {@code Empty} if
	 *         not found.
	 */
	Optional<Entry> getEntry(int entryID);

	/**
	 * Marks an entry as checked.
	 * 
	 * @param entryID The ID of the entry to check
	 * @throws IllegalStateException If the entry cannot be checked (e.g., entry
	 *                               does not exist)
	 */
	void checkEntry(int entryID);

	/**
	 * Marks an entry as unchecked.
	 * 
	 * @param entryID The ID of the entry to uncheck
	 * @throws IllegalStateException If the entry cannot be unchecked (e.g., entry
	 *                               does not exist)
	 */
	void uncheckEntry(int entryID);

	/**
	 * Updates the quantity of an entry.
	 * 
	 * @param entryID  The ID of the entry to update
	 * @param quantity The new quantity value
	 * @return Entry The entry with an updated quantity
	 */
	Entry updateQuantity(int entryID, int quantity);

	/**
	 * Adds a new entry.
	 * 
	 * @param entry The entry to add
	 * @return The saved entry with the new, correct database ID.
	 */
	Entry addEntry(Entry entry);

	/**
	 * Removes an entry by ID.
	 * 
	 * @param entryID The ID of the entry to remove
	 */
	void removeEntry(int entryID);
}

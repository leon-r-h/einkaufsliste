package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Entry;

public interface EntryRepository {
	
	List<Entry> getEntries(int userID);
	
	Optional<Entry> getEntry(int entryID);
	
	/**
	 * 
	 * @param entryID
	 * @throws IllegalStateException
	 */
	public void checkEntry(int entryID);
	
	/**
	 * 
	 * @param entryID
	 * @throws IllegalStateException
	 */
	public void uncheckEntry(int entryID);
	
	public void updateQuantity(int entryID, int quantity);
	
	public void addEntry(Entry entry);
	
	public void removeEntry(int entryID);
}

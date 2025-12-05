package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;

import com.siemens.einkaufsliste.database.model.Entry;

public final class EntryDatabaseRepository implements EntryRepository {

	public static final EntryDatabaseRepository REPOSITORY = new EntryDatabaseRepository();
	
	private EntryDatabaseRepository() {
		
	}
	
	@Override
	public List<Entry> getEntries(int userID) {
		return null;
	}

	@Override
	public Optional<Entry> getEntry(int entryID) {
		return Optional.empty();
	}

	@Override
	public void checkEntry(int entryID) {
		
	}

	@Override
	public void uncheckEntry(int entryID) {
		
	}

	@Override
	public void addEntry(Entry entry) {
		
	}

	@Override
	public void removeEntry(int entryID) {
		
	}

}

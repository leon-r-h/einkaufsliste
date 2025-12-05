package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.siemens.einkaufsliste.database.Database;

import com.siemens.einkaufsliste.database.model.Entry;

public final class EntryDatabaseRepository implements EntryRepository {

	public static final EntryDatabaseRepository REPOSITORY = new EntryDatabaseRepository();
	
	private EntryDatabaseRepository() {
		
	}
	
	private void checkAndCreate(){
		if (tableExists("Entry"))
			return;
		else
			createTable("Entry");

	}

	static boolean tableExists(String tableName) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = Database.getConnection();
			stmt = con.prepareStatement(
				"SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?");
			stmt.setString(1, con.getCatalog());
			stmt.setString(2, tableName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			throw new IllegalStateException();
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception e) {throw new IllegalStateException();}
			try { if (stmt != null) stmt.close(); } catch (Exception e) {throw new IllegalStateException();}
		}
		return false;
	}

	static void createTable(String tableName){
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = Database.getConnection();
			stmt = con.prepareStatement(
				"CREATE TABLE ?( entryID INT PRIMARY KEY, userID INT, productID INT, quantity INT, checkDate LocalDate");
				stmt.setString(1,tableName);
			} catch (Exception e) {
			throw new IllegalStateException();
		} finally {
			try { if (stmt != null) stmt.close(); } catch (Exception e) {throw new IllegalStateException();}
		}
	}

	@Override
	public List<Entry> getEntries(int userID) {
		List<Entry> entries = new ArrayList<>();

		
		return entries;
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

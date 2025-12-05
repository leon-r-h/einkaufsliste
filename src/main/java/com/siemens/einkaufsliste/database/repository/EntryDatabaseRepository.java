package com.siemens.einkaufsliste.database.repository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.siemens.einkaufsliste.database.model.Entry;

public final class EntryDatabaseRepository implements EntryRepository {

	EntryDatabaseRepository() {
		createIfNonExistent();
	}
	
	private void createIfNonExistent() {
		final String sql = """
				 CREATE TABLE IF NOT EXISTS entry (
				     entryID INT AUTO_INCREMENT PRIMARY KEY,
				     userID INT NOT NULL,
				     productID INT NOT NULL,
				     quantity INT,
				     checkDate DATE,

				     FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE,
				     FOREIGN KEY (productID) REFERENCES product(productID)
				 )
				""";

		try {
			Connection connection = Database.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}
	}

	@Override
	public List<Entry> getEntries(int userID) {
		List<Entry> entries = new ArrayList<>();
		Connection con;
		PreparedStatement stmt;
		ResultSet rs;
		try {
			con = Database.getConnection();
			stmt = con.prepareStatement("SELECT * FROM entry WHERE userID = ?");
			stmt.setInt(1,userID);
			
			rs = stmt.executeQuery();
			while (rs.next()) {
				entries.add(mapToEntry(rs));
			}
			return entries;
		} catch (SQLException e){
			throw new IllegalStateException();
		} finally {
        // try { if (rs != null) rs.close(); } catch (Exception e) { throw new IllegalStateException();}
        // try { if (stmt != null) stmt.close(); } catch (Exception e) { throw new IllegalStateException();}
    }
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

	private Entry mapToEntry(ResultSet rs) throws SQLException {
		return new Entry(
			rs.getInt("entryID"),
			rs.getInt("userID"),
			rs.getInt("productID"),
			rs.getInt("quantity"),
			rs.getDate("checkDate").toLocalDate()
		);
	}
}

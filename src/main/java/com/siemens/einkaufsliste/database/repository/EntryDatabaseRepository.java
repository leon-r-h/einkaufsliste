package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

				     FOREIGN KEY (userID) REFERENCES user(userID) ON DELETE CASCADE,
				     FOREIGN KEY (productID) REFERENCES product(productID)
				 )
				""";

		try {
			Connection connection = Database.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nukeEntries(int userID) {
		final String sql = """
				DELETE FROM entry
				WHERE userID = ?
				""";
		
		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
			stmt.setInt(1, userID);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Entry> getEntries(int userID) {
		List<Entry> entries = new ArrayList<>();
		final String sql = "SELECT * FROM entry WHERE userID = ? ORDER BY checkDate IS NOT NULL, checkDate ASC";
		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)){
			stmt.setInt(1,userID);
			
			try (ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				entries.add(mapToEntry(rs));
			}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return Collections.unmodifiableList(entries);
	}

	@Override
	public int totalPrice(int userID){
		final String sql = """
				SELECT SUM(price)
				FROM product, entry, user
				WHERE product.productID = entry.productID
				AND entry.userID = user.userID
				AND userID = ?
				""";

		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
			stmt.setInt(1, userID);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		return 0;
	}

	@Override
	public Optional<Entry> getEntry(int entryID) {
		final String sql = "SELECT * FROM entry WHERE entryID= ?";
		try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
			preparedStatement.setInt(1, entryID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToEntry(resultSet));
				}
			} 
		} catch (SQLException e) {
				e.printStackTrace();
		}

		return Optional.empty();
	}

	@Override
	public Entry checkEntry(int entryID) {
		final String sql = "UPDATE entry SET checkDate = ? WHERE entryID = ?";
		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
			
			stmt.setDate(1, Date.valueOf(LocalDate.now()));
			stmt.setInt(2, entryID);
			
			stmt.executeUpdate();

			Optional<Entry> entryOptional = getEntry(entryID);
			if (entryOptional.isPresent() == false) {
				throw new IllegalArgumentException();
			}
			return entryOptional.get();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Entry uncheckEntry(int entryID) {
		final String sql = "UPDATE entry SET checkDate = ? WHERE entryID = ?";
		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
			
			stmt.setDate(1, null);
			stmt.setInt(2, entryID);
			
			stmt.executeUpdate();
			
			Optional<Entry> entryOptional = getEntry(entryID);
			if (entryOptional.isPresent() == false) {
				throw new IllegalArgumentException();
			}
			return entryOptional.get();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Entry updateQuantity(int entryID, int quantity) {
		if (quantity < 1)
			throw new IllegalArgumentException();

		final String sql = "UPDATE entry SET quantity = ? WHERE entryID = ?";

		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
			stmt.setInt(1, quantity);
			stmt.setInt(2, entryID);

			stmt.executeUpdate();
			Optional<Entry> entryOptional = getEntry(entryID);
			if (entryOptional.isPresent() == false) {
				throw new IllegalArgumentException();
			}
			return entryOptional.get(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Entry addEntry(Entry entry) {
		// Wirf Exception, wenn die Menge ungültig ist oder das Entry schon ein checkDate hat (beides nicht erlaubt beim Anlegen)
		if (entry.quantity() < 1 || entry.checkDate() != null)
			throw new IllegalArgumentException(); // Ungültige Eingabedaten

		final String sql = "INSERT INTO entry (userID, productID, quantity, checkDate) VALUES (?, ?, ?, ?)";
		
		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, entry.userID());
			stmt.setInt(2, entry.productID());
			stmt.setInt(3, entry.quantity());
			stmt.setDate(4, null); // checkDate ist beim Anlegen immer null
			
			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new IllegalArgumentException(); // Es wurde kein Datensatz eingefügt
			}

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int newID = generatedKeys.getInt(1);

					return new Entry(newID, entry.userID(), entry.productID(), entry.quantity(), entry.checkDate());

				} else {
					throw new IllegalArgumentException(); // Es wurde kein neuer Schlüssel generiert
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(); // SQL-Fehler beim Einfügen
		}
	}

	@Override
	public void removeEntry(int entryID) {
		final String sql = "DELETE FROM entry WHERE entryID = ?";

		try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {	
			stmt.setInt(1, entryID);

			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Entry mapToEntry(ResultSet rs) throws SQLException {
		if (rs.getDate("checkDate") == null) {
			return new Entry(
				rs.getInt("entryID"),
				rs.getInt("userID"),
				rs.getInt("productID"),
				rs.getInt("quantity"),
				null
			);
		}
		else {
			return new Entry(
				rs.getInt("entryID"),
				rs.getInt("userID"),
				rs.getInt("productID"),
				rs.getInt("quantity"),
				rs.getDate("checkDate").toLocalDate()
			);
		}
	}
	/** 
	 * 
	 * Returns a positive int if budget is not exceeded by returned value.
	 * Returns a negative int if budget is exceeded by returned value.
	 * 
	 * */
	public int budgetTotalPriceDifference(int userID, int budget) {
		return budget-totalPrice(userID);
	}
}

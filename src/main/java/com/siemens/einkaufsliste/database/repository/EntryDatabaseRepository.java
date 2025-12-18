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

		try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nukeEntries(int userID) {
		final String sql = "DELETE FROM entry WHERE userID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Entry> getEntries(int userID) {
		List<Entry> entries = new ArrayList<>();
		final String sql = """
				SELECT entryID, userID, entry.productID, quantity, checkDate
				FROM entry, product
				WHERE entry.productID = product.productID
				AND userID = ?
				ORDER BY checkDate IS NOT NULL, product.name ASC, checkDate ASC
				""";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userID);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					entries.add(mapToEntry(resultSet));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Collections.unmodifiableList(entries);
	}

	@Override
	public int totalPrice(int userID) {
		final String sql = """
				SELECT SUM(price)
				FROM product, entry, user
				WHERE product.productID = entry.productID
				AND entry.userID = user.userID
				AND userID = ?
				""";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userID);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Optional<Entry> getEntry(int entryID) {
		final String sql = "SELECT * FROM entry WHERE entryID= ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, entryID);
			try (ResultSet resultSet = statement.executeQuery()) {
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
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setDate(1, Date.valueOf(LocalDate.now()));
			statement.setInt(2, entryID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

		return getEntry(entryID).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public Entry uncheckEntry(int entryID) {
		final String sql = "UPDATE entry SET checkDate = ? WHERE entryID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setDate(1, null);
			statement.setInt(2, entryID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		return getEntry(entryID).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public Entry updateQuantity(int entryID, int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException();
		}

		final String sql = "UPDATE entry SET quantity = ? WHERE entryID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, quantity);
			statement.setInt(2, entryID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

		return getEntry(entryID).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public Entry addEntry(Entry entry) {
		if (entry.quantity() < 1) {
			throw new IllegalArgumentException();
		}

		final String sql = "INSERT INTO entry (userID, productID, quantity, checkDate) VALUES (?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, entry.userID());
			statement.setInt(2, entry.productID());
			statement.setInt(3, entry.quantity());
			if (entry.checkDate() == null) {
				statement.setDate(4, null);
			} else {
				statement.setDate(4, Date.valueOf(entry.checkDate()));
			}

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new IllegalArgumentException();
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int newID = generatedKeys.getInt(1);
					return new Entry(newID, entry.userID(), entry.productID(), entry.quantity(), entry.checkDate());
				} else {
					throw new IllegalArgumentException();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void removeEntry(int entryID) {
		final String sql = "DELETE FROM entry WHERE entryID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, entryID);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Entry mapToEntry(ResultSet resultSet) throws SQLException {
		if (resultSet.getDate("checkDate") == null) {
			return new Entry(resultSet.getInt("entryID"), resultSet.getInt("userID"), resultSet.getInt("productID"),
					resultSet.getInt("quantity"), null);
		} else {
			return new Entry(resultSet.getInt("entryID"), resultSet.getInt("userID"), resultSet.getInt("productID"),
					resultSet.getInt("quantity"), resultSet.getDate("checkDate").toLocalDate());
		}
	}
}
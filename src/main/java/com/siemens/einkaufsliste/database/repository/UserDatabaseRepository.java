package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;

public final class UserDatabaseRepository implements UserRepository {

	private static final Logger LOGGER = Logger.getLogger(UserDatabaseRepository.class.getName());

	UserDatabaseRepository() throws DataAccessException {
		createIfNonExistent();
	}

	private void createIfNonExistent() throws DataAccessException {
		final String sql = """
				CREATE TABLE IF NOT EXISTS user (
				    userID INT AUTO_INCREMENT PRIMARY KEY,
				    firstName VARCHAR(255),
				    lastName VARCHAR(255),
				    birthDate DATE,
				    gender VARCHAR(50),
				    email VARCHAR(255) UNIQUE,
				    password VARCHAR(255),
				    newsletter BOOLEAN
				)
				""";
		try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public Optional<User> getUser(int userID) throws DataAccessException {
		final String sql = "SELECT * FROM user WHERE userId= ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, userID);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToUser(resultSet));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> getUser(String email) throws DataAccessException {
		final String sql = "SELECT * FROM user WHERE email = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, email);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToUser(resultSet));
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
		return Optional.empty();
	}

	@Override
	public boolean existsByEmail(String email) throws DataAccessException {
		final String sql = "SELECT 1 FROM user WHERE email = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, email);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public User registerUser(User user) throws IllegalArgumentException, DataAccessException {
		if (user.email().isBlank() || user.firstName().isBlank() || user.lastName().isBlank()
				|| user.password().isBlank() || user.password().length() < 4) {
			throw new IllegalArgumentException();
		}

		if (existsByEmail(user.email())) {
			throw new IllegalArgumentException();
		}

		final String sql = "INSERT INTO user (firstName, lastName, birthDate, gender, email, password, newsletter) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, user.firstName());
			preparedStatement.setString(2, user.lastName());
			preparedStatement.setDate(3, Date.valueOf(user.birthDate()));
			preparedStatement.setInt(4, user.gender().ordinal());
			preparedStatement.setString(5, user.email());
			preparedStatement.setString(6, user.password());
			preparedStatement.setBoolean(7, user.newsLetter());

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				throw new IllegalArgumentException();
			}

			try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int newID = generatedKeys.getInt(1);
					return new User(newID, user.firstName(), user.lastName(), user.birthDate(), user.gender(),
							user.email(), user.password(), user.newsLetter());
				} else {
					throw new IllegalArgumentException();
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public boolean deleteUser(int userID) throws DataAccessException {
		final String sql = "DELETE FROM user WHERE userID = ?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, userID);
			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	@Override
	public void updateUser(User user) throws DataAccessException {
		final String sql = "UPDATE user SET firstName=?, lastName=?, birthDate=?, gender=?, email=?, password=?, newsletter=? WHERE userID=?";
		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, user.firstName());
			preparedStatement.setString(2, user.lastName());
			preparedStatement.setDate(3, Date.valueOf(user.birthDate()));
			preparedStatement.setInt(4, user.gender().ordinal());
			preparedStatement.setString(5, user.email());
			preparedStatement.setString(6, user.password());
			preparedStatement.setBoolean(7, user.newsLetter());
			preparedStatement.setInt(8, user.userID());

			if (preparedStatement.executeUpdate() == 0) {
				throw new IllegalArgumentException();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new DataAccessException(e);
		}
	}

	private User mapToUser(ResultSet resultSet) throws SQLException {
		return new User(resultSet.getInt("userID"), resultSet.getString("firstName"), resultSet.getString("lastName"),
				resultSet.getDate("birthDate").toLocalDate(), Gender.values()[resultSet.getInt("gender")],
				resultSet.getString("email"), resultSet.getString("password"), resultSet.getBoolean("newsletter"));
	}
}
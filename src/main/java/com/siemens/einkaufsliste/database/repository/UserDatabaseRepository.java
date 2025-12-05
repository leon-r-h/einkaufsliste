package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.siemens.einkaufsliste.database.Database;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;

public final class UserDatabaseRepository implements UserRepository {

	public final static UserDatabaseRepository REPOSITORY = new UserDatabaseRepository();

	private UserDatabaseRepository() {
		Database.register(this::createIfNonExistent);
	}

	private void createIfNonExistent() {
		final String sql = """
				CREATE TABLE IF NOT EXISTS users (
				                id INT AUTO_INCREMENT PRIMARY KEY,
				                first_name VARCHAR(255),
				                last_name VARCHAR(255),
				                birth_date DATE,
				                gender VARCHAR(50),
				                email VARCHAR(255) UNIQUE,
				                password VARCHAR(255),
				            )
				""";

		try (Connection connection = Database.getConnection()) {
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}
	}

	@Override
	public Optional<User> getUser(int userID) {
		final String sql = "SELECT * FROM users WHERE id = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, userID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToUser(resultSet));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); // TOOD:
		}

		return Optional.empty();
	}

	@Override
	public Optional<User> getUser(String email) {
		final String sql = "SELECT * FROM users WHERE email = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, email);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(mapToUser(resultSet));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}

		return Optional.empty();
	}

	@Override
	public boolean existsByEmail(String email) {
		final String sql = "SELECT 1 FROM users WHERE email = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, email);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}

		return false;
	}

	@Override
	public User registerUser(User user) throws IllegalArgumentException {
		if (existsByEmail(user.email())) {
			throw new IllegalArgumentException();
		}

		final String sql = "INSERT INTO users (firstName, lastName, birthDate, gender, email, password, newsletter) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, user.firstName());
			preparedStatement.setString(2, user.lastName());
			preparedStatement.setDate(3, Date.valueOf(user.birthDate()));
			preparedStatement.setString(4, user.gender().name());
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
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean deleteUser(int userID) {
		final String sql = "DELETE FROM users WHERE id = ?";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, userID);
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}

		return false;
	}

	@Override
	public void updateUser(User user) {
		final String sql = "UPDATE users SET firstName=?, lastName=?, birthDate=?, gender=?, email=?, password=?, newsletter=? WHERE id=?";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, user.firstName());
			preparedStatement.setString(2, user.lastName());
			preparedStatement.setDate(3, Date.valueOf(user.birthDate()));
			preparedStatement.setString(4, user.gender().name());
			preparedStatement.setString(5, user.email());
			preparedStatement.setString(6, user.password());
			preparedStatement.setBoolean(7, user.newsLetter());

			preparedStatement.setInt(8, user.userID());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // TODO:
		}
	}

	private User mapToUser(ResultSet resultSet) throws SQLException {
		return new User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"),
				resultSet.getDate("birthDate").toLocalDate(), Gender.values()[resultSet.getInt("gender")],
				resultSet.getString("email"), resultSet.getString("password"), resultSet.getBoolean("newsletter"));
	}

}

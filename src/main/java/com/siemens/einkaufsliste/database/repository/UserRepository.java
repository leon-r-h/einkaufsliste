package com.siemens.einkaufsliste.database.repository;

import java.util.Optional;

import com.siemens.einkaufsliste.database.model.User;

/**
 * Handles user account management and identity.
 * <p>
 * This interface defines the rules for signing up ({@code registerUser}) and
 * finding existing users. It ensures we can look up a {@link User} by ID or
 * unique email to handle logins and profile updates.
 * </p>
 *
 * @author Leon Hoffmann
 */
public interface UserRepository {

	/**
	 * Retrieves a user by their unique database identifier.
	 *
	 * @param userID the unique ID of the user
	 * @return an {@link Optional} containing the {@link User} if found
	 * @throws DataAccessException if a database error occurs
	 */
	Optional<User> getUser(int userID) throws DataAccessException;

	/**
	 * Retrieves a user by their email address.
	 *
	 * @param email the email address to search for
	 * @return an {@link Optional} containing the {@link User} if found
	 * @throws DataAccessException if a database error occurs
	 */
	Optional<User> getUser(String email) throws DataAccessException;

	/**
	 * Checks if a user already exists with the given email address.
	 *
	 * @param email the email address to check
	 * @return {@code true} if the email is already in use, {@code false} otherwise
	 * @throws DataAccessException if a database error occurs
	 */
	boolean existsByEmail(String email) throws DataAccessException;

	/**
	 * Registers a new user in the system.
	 *
	 * @param user the {@link User} object containing registration details
	 * @return the created {@link User} with the generated database ID
	 * @throws DataAccessException      if a database error occurs
	 * @throws IllegalArgumentException if the user data is invalid (e.g., missing
	 *                                  fields or duplicate email)
	 */
	User registerUser(User user) throws DataAccessException, IllegalArgumentException;

	/**
	 * Deletes a user account from the system.
	 *
	 * @param userID the ID of the user to delete
	 * @return {@code true} if the user was successfully deleted, {@code false}
	 *         otherwise
	 * @throws DataAccessException if a database error occurs
	 */
	public boolean deleteUser(int userID) throws DataAccessException;

	/**
	 * Updates an existing user's profile information.
	 *
	 * @param user the {@link User} object containing the updated data
	 * @throws DataAccessException if a database error occurs or if the update fails
	 */
	void updateUser(User user) throws DataAccessException;
}

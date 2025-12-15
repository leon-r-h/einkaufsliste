package com.siemens.einkaufsliste.database.repository;

import java.util.Optional;

import com.siemens.einkaufsliste.database.model.User;

public interface UserRepository {
	
	/**
	 * Searches for a user by ID.
	 * 
	 * @param userID The ID of the user
	 * @return An {@link Optional} containing the {@link User}, or {@code Empty} if not found.
	 */
	public Optional<User> getUser(int userID);
	
	/**
	 * Searches for a user by email.
	 * 
	 * @param email The email address
	 * @return An {@link Optional} containing the {@link User}, or {@code Empty} if not found.
	 */
	Optional<User> getUser(String email);
	
	/**
	 * Checks whether an email is already taken.
	 * 
	 * @param email The email to check
	 * @return {@code true} if the email already exists in the database.
	 */
	boolean existsByEmail(String email);
	
	/**
	 * Registers a new user.
	 * 
	 * @param user The new user
	 * @return The saved user with the new, correct database ID.
	 * @throws IllegalArgumentException If the email is already taken.
	 */
	User registerUser(User user) throws IllegalArgumentException;

	/**
	 * Deletes a user.
	 * 
	 * @param userID The ID of the user to delete.
	 * @return {@code true} if a user was deleted; {@code false} if the ID did not exist.
	 */
	public boolean deleteUser(int userID);
	
	/**
	 * Updates an existing user.
	 * The ID is taken directly from the User object.
	 * 
	 * @param user The user with the new data (and the existing ID).
	 * @return The updated user.
	 * @throws IllegalArgumentException If the user or the ID is invalid.
	 */
	void updateUser(User user);
}

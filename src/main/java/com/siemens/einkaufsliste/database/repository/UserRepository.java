package com.siemens.einkaufsliste.database.repository;

import java.util.Optional;

import com.siemens.einkaufsliste.database.model.User;

public interface UserRepository {

	Optional<User> getUser(int userID) throws DataAccessException;

	Optional<User> getUser(String email) throws DataAccessException;

	boolean existsByEmail(String email) throws DataAccessException;

	User registerUser(User user) throws DataAccessException, IllegalArgumentException;

	public boolean deleteUser(int userID) throws DataAccessException;

	void updateUser(User user) throws DataAccessException;
}

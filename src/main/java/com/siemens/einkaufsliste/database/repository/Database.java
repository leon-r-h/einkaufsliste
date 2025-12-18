package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

	private Database() {
		
	}
	
	private static final String URL_BASE = "jdbc:mysql://tramspotters.ddnss.de/Gruppe1";
	private static final String USER_NAME = "user1";
	private static final String USER_PASSWORD = "passwort1";
	
	private static Connection connection;
	
	private static UserRepository users;
	private static ProductRepository products; 
	private static EntryRepository entries;
	
	/**
	 * Retrieves the user repository.
	 * 
	 * @return The {@link UserRepository} instance
	 * @throws IllegalStateException If the database is not connected
	 */
	public static UserRepository getUsers() {
		if(users == null) {
			throw new IllegalStateException();
		}
		
		return users;
	}
	
	/**
	 * Retrieves the product repository.
	 * 
	 * @return The {@link ProductRepository} instance
	 * @throws IllegalStateException If the database is not connected
	 */
	public static ProductRepository getProducts() {
		if(products == null) {
			throw new IllegalStateException();
		}
		
		return products;
	}
	
	/**
	 * Retrieves the entry repository.
	 * 
	 * @return The {@link EntryRepository} instance
	 * @throws IllegalStateException If the database is not connected
	 */
	public static EntryRepository getEntries() {
		if(entries == null) {
			throw new IllegalStateException();
		}
		
		return entries;
	}
	
	/**
	 * Connects to the database.
	 * 
	 * @throws IllegalStateException If a connection is already established
	 * @throws RuntimeException If another error occurs.
	 */
	public static void connect() {
		try {
			if(connection != null && !connection.isClosed()) {
				throw new IllegalStateException();
			}
			
			connection = DriverManager.getConnection(URL_BASE, USER_NAME, USER_PASSWORD);
			
			users = new UserDatabaseRepository();
			products = new ProductDatabaseRepository();
			entries = new EntryDatabaseRepository();
		} catch(SQLException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Returns the existing connection.
	 * 
	 * @return The active {@link Connection}
	 * @throws IllegalStateException If no connection exists or the connection is closed
	 */
	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				throw new IllegalStateException();
			}
		} catch (SQLException e) {
			throw new IllegalStateException();
		}
		
		return connection;
	}

	/**
	 * Closes the connection.
	 * 
	 * @throws IllegalStateException If an error occurs while closing the connection.
	 */
	public static void disconnect() {
		if(connection == null) {
			throw new IllegalStateException();
		}
		
		try {
			connection.close();
		} catch (SQLException e) {
			throw new IllegalStateException();
		}
		
		users = null;
		products = null;
		entries = null;

		connection = null;
		
		System.gc();
	}
}

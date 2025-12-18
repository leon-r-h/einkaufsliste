package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {

	private Database() {

	}

	private static final String URL_BASE = "jdbc:mysql://tramspotters.ddnss.de/Gruppe1";
	private static final String USER_NAME = "user1";
	private static final String USER_PASSWORD = "passwort1";

	private static HikariDataSource dataSource;

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
		if (users == null) {
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
		if (products == null) {
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
		if (entries == null) {
			throw new IllegalStateException();
		}

		return entries;
	}

	/**
	 * Connects to the database.
	 *
	 * @throws IllegalStateException If a connection is already established
	 * @throws RuntimeException      If another error occurs.
	 */
	public static void connect() {
		if (dataSource != null && !dataSource.isClosed()) {
			throw new IllegalStateException();
		}

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(URL_BASE);
		config.setUsername(USER_NAME);
		config.setPassword(USER_PASSWORD);

		config.setMaximumPoolSize(10);
		config.setMinimumIdle(2);
		config.setIdleTimeout(30000);
		config.setConnectionTimeout(20000);
		config.setPoolName("FormulaEmendiPool");

		dataSource = new HikariDataSource(config);

		users = new UserDatabaseRepository();
		products = new ProductDatabaseRepository();
		entries = new EntryDatabaseRepository();
	}

	/**
	 * Returns the existing connection.
	 *
	 * @return The active {@link Connection}
	 * @throws SQLException
	 * @throws IllegalStateException If no connection exists or the connection is
	 *                               closed
	 */
	public static Connection getConnection() throws SQLException {
		if (dataSource == null || dataSource.isClosed()) {
			throw new IllegalStateException();
		}

		return dataSource.getConnection();
	}

	/**
	 * Closes the connection.
	 *
	 * @throws IllegalStateException If an error occurs while closing the
	 *                               connection.
	 */
	public static void disconnect() {
		System.out.println(dataSource);

		if (dataSource == null || dataSource.isClosed()) {
			throw new IllegalStateException();
		}

		dataSource.close();
		dataSource = null;

		users = null;
		products = null;
		entries = null;

		System.gc();
	}
}

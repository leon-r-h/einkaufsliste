package com.siemens.einkaufsliste.database.repository;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The central base class for data access.
 * <p>
 * This singleton manages the database connection pool and acts as the "Factory"
 * for retrieving specific repositories like {@link UserRepository} or
 * {@link ProductRepository}. Ideally, you just call {@code connect()} once and
 * then use what you need.
 * </p>
 *
 * @author Leon Hoffmann
 */
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
	 * Retrieves the singleton instance of the User repository.
	 *
	 * @return the active {@link UserRepository} instance
	 * @throws IllegalStateException if the {@link #connect()} method has not been
	 *                               called yet
	 */
	public static UserRepository getUsers() {
		if (users == null) {
			throw new IllegalStateException();
		}

		return users;
	}

	/**
	 * Retrieves the singleton instance of the Product repository.
	 *
	 * @return the active {@link ProductRepository} instance
	 * @throws IllegalStateException if the {@link #connect()} method has not been
	 *                               called yet
	 */
	public static ProductRepository getProducts() {
		if (products == null) {
			throw new IllegalStateException();
		}

		return products;
	}

	/**
	 * Retrieves the singleton instance of the Entry repository.
	 *
	 * @return the active {@link EntryRepository} instance
	 * @throws IllegalStateException if the {@link #connect()} method has not been
	 *                               called yet
	 */
	public static EntryRepository getEntries() {
		if (entries == null) {
			throw new IllegalStateException();
		}

		return entries;
	}

	/**
	 * Initializes the connection pool and instantiates the repositories.
	 * <p>
	 * This method must be called exactly once at application startup.
	 * </p>
	 *
	 * @throws DataAccessException   if the database driver cannot be loaded or
	 *                               configuration fails
	 * @throws IllegalStateException if a connection pool is already active
	 */
	public static void connect() throws DataAccessException {
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
	 * Borrows a connection from the underlying connection pool.
	 * <p>
	 * Callers are responsible for closing the returned connection to return it to
	 * the pool.
	 * </p>
	 *
	 * @return an active {@link Connection} to the database
	 * @throws SQLException          if a database access error occurs
	 * @throws IllegalStateException if the database has not been initialized via
	 *                               {@link #connect()}
	 */
	public static Connection getConnection() throws SQLException {
		if (dataSource == null || dataSource.isClosed()) {
			throw new IllegalStateException();
		}

		return dataSource.getConnection();
	}

	/**
	 * Closes the connection pool and releases all resources.
	 * <p>
	 * This should be called during application shutdown.
	 * </p>
	 *
	 * @throws IllegalStateException if the database is already closed or was never
	 *                               initialized
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

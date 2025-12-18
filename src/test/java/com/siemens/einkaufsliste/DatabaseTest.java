package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.siemens.einkaufsliste.database.repository.Database;

public class DatabaseTest {

	@BeforeEach
	@AfterEach
	void resetDatabaseState() {
		try {
			Database.disconnect();
		} catch (Exception e) {
		}
	}

	@Test
	@DisplayName("Should establish a valid connection")
	void shouldConnectSuccessfully() throws SQLException {
		Database.connect();

		try (Connection connection = Database.getConnection()) {
			assertNotNull(connection, "Connection should not be null");
			assertFalse(connection.isClosed(), "Connection should be open");
			assertTrue(connection.isValid(2), "Connection should be valid (pingable)");
		}
	}

	@Test
	@DisplayName("Calling connect() twice should be safe (Idempotent)")
	void multipleConnectCallsShouldNotThrow() {
		Database.connect();
		assertThrows(IllegalStateException.class, Database::connect);
	}

	@Test
	@DisplayName("Repositories should be accessible only after connection")
	void repositoriesShouldBeInitialized() {
		assertThrows(IllegalStateException.class, Database::getUsers);
		assertThrows(IllegalStateException.class, Database::getProducts);
		assertThrows(IllegalStateException.class, Database::getEntries);

		Database.connect();
		assertNotNull(Database.getUsers());
		assertNotNull(Database.getProducts());
		assertNotNull(Database.getEntries());
	}

	@Test
	@DisplayName("Disconnect should reset state and Repositories")
	void disconnectShouldResetRepositories() {
		Database.connect();
		assertNotNull(Database.getUsers());

		Database.disconnect();

		assertThrows(IllegalStateException.class, Database::getUsers);
		assertThrows(IllegalStateException.class, Database::getConnection);
	}
}
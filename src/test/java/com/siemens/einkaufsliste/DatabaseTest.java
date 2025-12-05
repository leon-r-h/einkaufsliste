package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DatabaseTest {

	@Test
	@DisplayName("Sanity Check")
	void shouldAlwaysBeTrue() {
		assertTrue(true);
	}

	@Test
	@DisplayName("Second Database Connection Throws Error")
	void secondDatabaseConnectionShouldThrowError() {
		com.siemens.einkaufsliste.database.repository.Database.connect();
		assertThrows(IllegalStateException.class, () -> {
			com.siemens.einkaufsliste.database.repository.Database.connect();
		});
		
	}

	@Test
	@DisplayName("Database disconnection before connection throws Error")
	void databaseDisconnectionBeforeConnectionThrowsError() {
		assertThrows(IllegalStateException.class, () -> {
			com.siemens.einkaufsliste.database.repository.Database.disconnect();
		});
	}

	@Test
	@DisplayName("DatabaseIsNotNullAfterConnection")
	void databaseIsNotNullAfterConnection() {
		com.siemens.einkaufsliste.database.repository.Database.connect();
		assertTrue(!(com.siemens.einkaufsliste.database.repository.Database.getConnection() == null));
	}
}

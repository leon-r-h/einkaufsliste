package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.Connection;

public class DatabaseTest {

	@Test
	@DisplayName("Sanity Check")
	void shouldAlwaysBeTrue() {
		assertTrue(true);
	}

	@Test
	@DisplayName("Second Database Connection Throws Error")
	void secondDatabaseConnectionShouldThrowError() {
		com.siemens.einkaufsliste.database.Database.connect();
		assertThrows(IllegalStateException.class, () -> {
			com.siemens.einkaufsliste.database.Database.connect();
		});
		
	}

	@Test
	@DisplayName("Database disconnection before connection throws Error")
	void databaseDisconnectionBeforeConnectionThrowsError() {
		assertThrows(IllegalStateException.class, () -> {
			com.siemens.einkaufsliste.database.Database.disconnect();
		});
	}

	@Test
	@DisplayName("DatabaseIsNotNullAfterConnection")
	void databaseIsNotNullAfterConnection() {
		com.siemens.einkaufsliste.database.Database.connect();
		assertTrue(!(com.siemens.einkaufsliste.database.Database.getConnection() == null));
	}
}

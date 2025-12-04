package com.siemens.einkaufsliste.database;

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
	
	private static void connect(String name, String password) {
		try {
			if(connection != null && !connection.isClosed()) {
				throw new IllegalStateException();
			}
			
			connection = DriverManager.getConnection(URL_BASE, USER_NAME, USER_PASSWORD);
			
			System.out.println("DEBUG: Verbindung zu Datenbank hergestellt!"); // TODO: Entfernen debug
		} catch(SQLException e) {
			throw new RuntimeException();
		}
	}
	
	public static void connect() {
		connect("user1", "passwort1");
	}

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

	public static void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new IllegalStateException();
		}
	}
}

package com.siemens.einkaufsliste.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public final class Database {

	private Database() {

	}
	
	private static final String URL_BASE = "jdbc:mysql://tramspotters.ddnss.de/Gruppe1";
	private static final String USER_NAME = "user1";
	private static final String USER_PASSWORD = "passwort1";
	
	private static Connection connection;
	
	private static ArrayList<ConnectionListener> connectionListeners = new ArrayList<>();
	
	/**
	 * Verbindet sich mit der Datenbank.
	 * 
	 * @throws IllegalStateException falls Verbindung schon hergestellt
	 * @throws RuntimeException falls ein anderer Fehler auftritt.
	 */
	public static void connect() {
		try {
			if(connection != null && !connection.isClosed()) {
				throw new IllegalStateException();
			}
			
			connection = DriverManager.getConnection(URL_BASE, USER_NAME, USER_PASSWORD);
			connectionListeners.forEach(ConnectionListener::connected);
		} catch(SQLException e) {
			throw new RuntimeException();
		}
	}
	
	public static void register(ConnectionListener listener) {
		if(connection != null) {
			return;
		}
		
		connectionListeners.add(listener);
	}

	/**
	 * Gibt die bestehende Verbindung zurück.
	 * 
	 * @return {@code null} wenn keine Verbindung vorhanden, sonst laufende {@link Connection}.
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
	 * Beendet die Verbindung.
	 * 
	 * @throws IllegalStateException falls ein Fehler beim Beenden auftritt.
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

		connection = null;
	}
}

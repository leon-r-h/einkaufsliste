package com.siemens.einkaufsliste.database.repository;

import java.util.Optional;

import com.siemens.einkaufsliste.database.model.User;

public interface UserRepository {
	
	/**
	 * Sucht einen Nutzer anhand der ID.
	 * 
	 * @param userID Die ID des Nutzers
	 * @return Ein Optional mit dem User, oder Empty falls nicht gefunden.
	 */
	public Optional<User> getUser(int userID);
	
	/**
	 * Sucht einen Nutzer anhand der E-Mail.
	 * 
	 * @param email Die E-Mail-Adresse
	 * @return Ein Optional mit dem User, oder Empty falls nicht gefunden.
	 */
	Optional<User> getUser(String email);
	
	/**
	 * Prüft, ob eine E-Mail bereits vergeben ist.
	 * 
	 * @param email Die zu prüfende E-Mail
	 * @return true, wenn die E-Mail bereits in der DB existiert.
	 */
	boolean existsByEmail(String email);
	
	/**
	 * Registriert einen neuen Nutzer.
	 * @param user Der neue Nutzer
	 * @return Der gespeicherte Nutzer mit der neuen, korrekten Datenbank-ID.
	 * @throws IllegalArgumentException Wenn die E-Mail bereits vergeben ist.
	 */
	User registerUser(User user) throws IllegalArgumentException;

	/**
	 * Löscht einen Nutzer.
	 * @param userID Die ID des zu löschenden Nutzers.
	 * @return true, wenn ein Nutzer gelöscht wurde; false, wenn die ID nicht existierte.
	 */
	public boolean deleteUser(int userID);
	
	/**
	 * Aktualisiert einen bestehenden Nutzer.
	 * Die ID wird direkt aus dem User-Objekt genommen.
	 * 
	 * @param user Der Nutzer mit den neuen Daten (und der existierenden ID).
	 * @return Der aktualisierte Nutzer.
	 * @throws IllegalArgumentException Wenn der User oder die ID ungültig ist.
	 */
	void updateUser(User user);
}

package com.siemens.einkaufsliste.database.model;

import java.time.LocalDate;

/**
 * Represents the person holding the shopping list.
 * <p>
 * This record stores the core account identity. It holds credentials like
 * {@code email} and {@code password} used for authentication, and profile
 * details like {@code gender} and {@code birthDate}.
 * </p>
 *
 * @author Leon Hoffmann
 * @author Hakan Tosun
 * @author Georg Busche
 * @author Nils Gohr
 */
public record User(int userID, String firstName, String lastName, LocalDate birthDate, Gender gender, String email,
		String password, boolean newsLetter) {

	public enum Gender {
		MALE, FEMALE, OTHER
	}

}
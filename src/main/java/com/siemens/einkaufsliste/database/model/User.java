package com.siemens.einkaufsliste.database.model;

import java.time.LocalDate;

public record User(int userID, String firstName, String lastName, LocalDate birthDate, Gender gender, String email, String password,
		boolean newsLetter) {
	
	public User(String firstName, String lastName, LocalDate birthDate, Gender gender, String email, String password,
			boolean newsLetter) {
		this(0, firstName, lastName, birthDate, gender, email, password, newsLetter);
	}
	
	public enum Gender {
		MALE,
		FEMALE,
		OTHER
	}

}
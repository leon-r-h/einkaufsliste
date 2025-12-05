package com.siemens.einkaufsliste.database.model;

public record User(int userID, String firstName, String lastName, int birthDate, Gender gender, String email, String password,
		boolean newsLetter) {
	
	public enum Gender {
		MALE,
		FEMALE,
		OTHER
	}

}
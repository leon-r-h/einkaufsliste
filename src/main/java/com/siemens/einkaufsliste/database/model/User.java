package com.siemens.einkaufsliste.database.model;

public record User(int userID, String firstName, String lastName, int birthDate, String email, String password,
		boolean newsLetter) {

}
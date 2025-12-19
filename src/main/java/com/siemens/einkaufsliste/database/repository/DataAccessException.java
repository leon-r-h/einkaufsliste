package com.siemens.einkaufsliste.database.repository;

public final class DataAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataAccessException() {

	}

	public DataAccessException(Throwable cause) {
		super(cause);
	}

}

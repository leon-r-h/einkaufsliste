package com.siemens.einkaufsliste;

import com.siemens.einkaufsliste.database.Database;

public final class Main {

	private Main() {
		Database.connect();
	}

	public static void main(String[] args) {
		new Main();
	}

}

package com.siemens.einkaufsliste;

public final class Main {

	private Main() {
		Database database = Database.instance();
		database.connect("", "");
		
		Database.Product product;
		
		
		
		database.disconnect();
	}

	public static void main(String[] args) {
		new Main();
	}

}

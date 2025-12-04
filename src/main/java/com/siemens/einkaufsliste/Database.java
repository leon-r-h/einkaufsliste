package com.siemens.einkaufsliste;

public final class Database {
	
	public record Product(int id, String name, String category) {
		
	}
	
	private Database() {
		
	}
	
	
	private static final Database DATABASE = new Database();
	
	public static Database instance() {
		return DATABASE;
	}
	
	public void connect(String name, String password) {

	}
	
	public void disconnect() {
		
	}
	
	public void setName(int id, String name) {
		
	}
	
	public void addProduct(Product product) {
		
	}
}

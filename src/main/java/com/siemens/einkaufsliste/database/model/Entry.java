package com.siemens.einkaufsliste.database.model;

public record Entry(int entryID, int userID, int productID, int quantity, Integer checkDate) {

}

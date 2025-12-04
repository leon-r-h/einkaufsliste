package com.siemens.einkaufsliste.database.model;

public record ShoppingListEntry(int shoppingListEntryID, int userID, int productID, int quantity, Integer checkDate) {

}

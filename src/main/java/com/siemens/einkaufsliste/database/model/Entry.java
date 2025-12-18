package com.siemens.einkaufsliste.database.model;

import java.time.LocalDate;

public record Entry(int entryID, int userID, int productID, int quantity, LocalDate checkDate) {

}

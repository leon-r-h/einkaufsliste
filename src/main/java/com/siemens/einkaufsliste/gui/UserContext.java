package com.siemens.einkaufsliste.gui;

import java.util.Optional;

import com.siemens.einkaufsliste.database.model.User;

@FunctionalInterface
public interface UserContext {
	
	Optional<User> getCurrentUser();

}

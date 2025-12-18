package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.UserRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class UserDatabaseRepositoryTest {

	private static UserRepository userRepository;
	private static User testUser;

	@BeforeAll
	static void setupDatabase() {
		Database.connect();
		userRepository = Database.getUsers();
	}

	@AfterAll
	static void teardownDatabase() {
		Database.disconnect();
	}

	@Test
	@Order(1)
	@DisplayName("Register Test User - Success")
	void registerTestUser() {
		testUser = new User(0, "Max", "Müller", LocalDate.of(1990, 3, 15), User.Gender.MALE,
				"max.mueller.test@email.de", "Pass123!", true);

		User registeredUser = userRepository.registerUser(testUser);

		assertNotNull(registeredUser);
		assertTrue(registeredUser.userID() > 0, "User ID sollte größer als 0 sein");
		assertEquals(testUser.firstName(), registeredUser.firstName());
		assertEquals(testUser.email(), registeredUser.email());

		testUser = registeredUser;
	}

	@Test
	@Order(2)
	@DisplayName("Register User - Duplicate Email throws Exception")
	void registerUserDuplicateEmail() {
		User duplicateUser = new User(0, "Anna", "Schmidt", LocalDate.of(1985, 7, 22), User.Gender.FEMALE,
				"max.mueller.test@email.de", "Pass456!", false);

		assertThrows(IllegalArgumentException.class, () -> {
			userRepository.registerUser(duplicateUser);
		});
	}

	@Test
	@Order(3)
	@DisplayName("Get User by ID - Success")
	void getUserById() {
		Optional<User> foundUser = userRepository.getUser(testUser.userID());

		assertTrue(foundUser.isPresent(), "User sollte gefunden werden");
		assertEquals(testUser.email(), foundUser.get().email());
		assertEquals(testUser.firstName(), foundUser.get().firstName());
	}

	@Test
	@Order(4)
	@DisplayName("Get User by ID - Not Found")
	void getUserByIdNotFound() {
		Optional<User> foundUser = userRepository.getUser(999999);

		assertFalse(foundUser.isPresent(), "User sollte nicht gefunden werden");
	}

	@Test
	@Order(5)
	@DisplayName("Get User by Email - Success")
	void getUserByEmail() {
		Optional<User> foundUser = userRepository.getUser("max.mueller.test@email.de");

		assertTrue(foundUser.isPresent(), "User sollte gefunden werden");
		assertEquals(testUser.firstName(), foundUser.get().firstName());
		assertEquals(testUser.lastName(), foundUser.get().lastName());
	}

	@Test
	@Order(6)
	@DisplayName("Get User by Email - Not Found")
	void getUserByEmailNotFound() {
		Optional<User> foundUser = userRepository.getUser("nichtexistent@email.de");

		assertFalse(foundUser.isPresent(), "User sollte nicht gefunden werden");
	}

	@Test
	@Order(7)
	@DisplayName("Exists by Email - True")
	void existsByEmailTrue() {
		boolean exists = userRepository.existsByEmail("max.mueller.test@email.de");

		assertTrue(exists, "E-Mail sollte existieren");
	}

	@Test
	@Order(8)
	@DisplayName("Exists by Email - False")
	void existsByEmailFalse() {
		boolean exists = userRepository.existsByEmail("nichtexistent@email.de");

		assertFalse(exists, "E-Mail sollte nicht existieren");
	}

	@Test
	@Order(9)
	@DisplayName("Update User - Success")
	void updateUser() {
		User updatedUser = new User(testUser.userID(), "Maximilian", "Müller-Schmidt", LocalDate.of(1990, 3, 15),
				User.Gender.MALE, "max.mueller.test@email.de", "NewPass789!", false);

		assertDoesNotThrow(() -> {
			userRepository.updateUser(updatedUser);
		});

		Optional<User> foundUser = userRepository.getUser(testUser.userID());
		assertTrue(foundUser.isPresent());
		assertEquals("Maximilian", foundUser.get().firstName());
		assertEquals("Müller-Schmidt", foundUser.get().lastName());
		assertFalse(foundUser.get().newsLetter());
	}

	@Test
	@Order(10)
	@DisplayName("Update User - Invalid ID throws Exception")
	void updateUserInvalidId() {
		User invalidUser = new User(999999, "Test", "User", LocalDate.of(1990, 1, 1), User.Gender.MALE, "test@email.de",
				"Pass123!", true);

		assertThrows(IllegalArgumentException.class, () -> {
			userRepository.updateUser(invalidUser);
		});
	}

	@Test
	@Order(11)
	@DisplayName("Delete User - Success")
	void deleteUser() {
		boolean deleted = userRepository.deleteUser(testUser.userID());

		assertTrue(deleted, "User sollte gelöscht worden sein");

		Optional<User> foundUser = userRepository.getUser(testUser.userID());
		assertFalse(foundUser.isPresent(), "Gelöschter User sollte nicht mehr gefunden werden");
	}

	@Test
	@Order(12)
	@DisplayName("Delete User - Not Found")
	void deleteUserNotFound() {
		boolean deleted = userRepository.deleteUser(999999);

		assertFalse(deleted, "Nicht existierender User kann nicht gelöscht werden");
	}

	@Test
	@Order(13)
	@DisplayName("Register Multiple Users")
	void registerMultipleUsers() {
		User user1 = new User(0, "Anna", "Schmidt", LocalDate.of(1985, 7, 22), User.Gender.FEMALE,
				"anna.schmidt.test@email.de", "Secure456", false);
		User user2 = new User(0, "Thomas", "Weber", LocalDate.of(1992, 11, 8), User.Gender.MALE,
				"thomas.weber.test@email.de", "MyPass789", true);
		User user3 = new User(0, "Julia", "Wagner", LocalDate.of(1988, 5, 30), User.Gender.FEMALE,
				"julia.wagner.test@email.de", "Julia2023!", false);

		User registered1 = userRepository.registerUser(user1);
		User registered2 = userRepository.registerUser(user2);
		User registered3 = userRepository.registerUser(user3);

		assertNotNull(registered1);
		assertNotNull(registered2);
		assertNotNull(registered3);

		assertTrue(registered1.userID() > 0);
		assertTrue(registered2.userID() > 0);
		assertTrue(registered3.userID() > 0);

		// Cleanup
		userRepository.deleteUser(registered1.userID());
		userRepository.deleteUser(registered2.userID());
		userRepository.deleteUser(registered3.userID());
	}

	@Test
	@Order(14)
	@DisplayName("Test All Genders")
	void testAllGenders() {
		User maleUser = new User(0, "John", "Doe", LocalDate.of(1990, 1, 1), User.Gender.MALE, "john.doe.test@email.de",
				"Pass123!", true);
		User femaleUser = new User(0, "Jane", "Doe", LocalDate.of(1991, 2, 2), User.Gender.FEMALE,
				"jane.doe.test@email.de", "Pass456!", true);
		User otherUser = new User(0, "Alex", "Doe", LocalDate.of(1992, 3, 3), User.Gender.OTHER,
				"alex.doe.test@email.de", "Pass789!", true);

		User registered1 = userRepository.registerUser(maleUser);
		User registered2 = userRepository.registerUser(femaleUser);
		User registered3 = userRepository.registerUser(otherUser);

		assertEquals(User.Gender.MALE, registered1.gender());
		assertEquals(User.Gender.FEMALE, registered2.gender());
		assertEquals(User.Gender.OTHER, registered3.gender());

		// Cleanup
		userRepository.deleteUser(registered1.userID());
		userRepository.deleteUser(registered2.userID());
		userRepository.deleteUser(registered3.userID());
	}
}
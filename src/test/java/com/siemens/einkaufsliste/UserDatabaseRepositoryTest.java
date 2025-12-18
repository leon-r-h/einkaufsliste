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
	static void setupDatabase() throws Exception {
		Database.connect();
		userRepository = Database.getUsers();
	}

	@AfterAll
	static void teardownDatabase() {
		Database.disconnect();
	}

	@Test
	@Order(1)
	@DisplayName("Should successfully register a test user")
	void registerTestUser() throws Exception {
		testUser = new User(0, "Max", "Miller", LocalDate.of(1990, 3, 15), User.Gender.MALE, "max.miller.test@email.de",
				"Pass123!", true);

		User registeredUser = userRepository.registerUser(testUser);

		assertNotNull(registeredUser);
		assertTrue(registeredUser.userID() > 0, "User ID should be greater than 0");
		assertEquals(testUser.firstName(), registeredUser.firstName());
		assertEquals(testUser.email(), registeredUser.email());

		testUser = registeredUser;
	}

	@Test
	@Order(2)
	@DisplayName("Registering user with duplicate email should throw Exception")
	void registerUserDuplicateEmail() {
		User duplicateUser = new User(0, "Anna", "Smith", LocalDate.of(1985, 7, 22), User.Gender.FEMALE,
				"max.miller.test@email.de", "Pass456!", false);

		assertThrows(IllegalArgumentException.class, () -> {
			userRepository.registerUser(duplicateUser);
		});
	}

	@Test
	@Order(3)
	@DisplayName("Should retrieve user by ID successfully")
	void getUserById() throws Exception {
		Optional<User> foundUser = userRepository.getUser(testUser.userID());

		assertTrue(foundUser.isPresent(), "User should be found");
		assertEquals(testUser.email(), foundUser.get().email());
		assertEquals(testUser.firstName(), foundUser.get().firstName());
	}

	@Test
	@Order(4)
	@DisplayName("Should return empty optional for invalid user ID")
	void getUserByIdNotFound() throws Exception {
		Optional<User> foundUser = userRepository.getUser(999999);

		assertFalse(foundUser.isPresent(), "User should not be found");
	}

	@Test
	@Order(5)
	@DisplayName("Should retrieve user by Email successfully")
	void getUserByEmail() throws Exception {
		Optional<User> foundUser = userRepository.getUser("max.miller.test@email.de");

		assertTrue(foundUser.isPresent(), "User should be found");
		assertEquals(testUser.firstName(), foundUser.get().firstName());
		assertEquals(testUser.lastName(), foundUser.get().lastName());
	}

	@Test
	@Order(6)
	@DisplayName("Should return empty optional for invalid Email")
	void getUserByEmailNotFound() throws Exception {
		Optional<User> foundUser = userRepository.getUser("nonexistent@email.de");

		assertFalse(foundUser.isPresent(), "User should not be found");
	}

	@Test
	@Order(7)
	@DisplayName("Should confirm email exists")
	void existsByEmailTrue() throws Exception {
		boolean exists = userRepository.existsByEmail("max.miller.test@email.de");

		assertTrue(exists, "Email should exist");
	}

	@Test
	@Order(8)
	@DisplayName("Should confirm email does not exist")
	void existsByEmailFalse() throws Exception {
		boolean exists = userRepository.existsByEmail("nonexistent@email.de");

		assertFalse(exists, "Email should not exist");
	}

	@Test
	@Order(9)
	@DisplayName("Should successfully update user information")
	void updateUser() throws Exception {
		User updatedUser = new User(testUser.userID(), "Maximilian", "Miller-Smith", LocalDate.of(1990, 3, 15),
				User.Gender.MALE, "max.miller.test@email.de", "NewPass789!", false);

		assertDoesNotThrow(() -> {
			userRepository.updateUser(updatedUser);
		});

		Optional<User> foundUser = userRepository.getUser(testUser.userID());
		assertTrue(foundUser.isPresent());
		assertEquals("Maximilian", foundUser.get().firstName());
		assertEquals("Miller-Smith", foundUser.get().lastName());
		assertFalse(foundUser.get().newsLetter());
	}

	@Test
	@Order(10)
	@DisplayName("Updating user with invalid ID should throw Exception")
	void updateUserInvalidId() {
		User invalidUser = new User(999999, "Test", "User", LocalDate.of(1990, 1, 1), User.Gender.MALE, "test@email.de",
				"Pass123!", true);

		assertThrows(IllegalArgumentException.class, () -> {
			userRepository.updateUser(invalidUser);
		});
	}

	@Test
	@Order(11)
	@DisplayName("Should successfully delete user")
	void deleteUser() throws Exception {
		boolean deleted = userRepository.deleteUser(testUser.userID());

		assertTrue(deleted, "User should have been deleted");

		Optional<User> foundUser = userRepository.getUser(testUser.userID());
		assertFalse(foundUser.isPresent(), "Deleted user should not be found");
	}

	@Test
	@Order(12)
	@DisplayName("Deleting non-existent user should return false")
	void deleteUserNotFound() throws Exception {
		boolean deleted = userRepository.deleteUser(999999);

		assertFalse(deleted, "Non-existing user cannot be deleted");
	}

	@Test
	@Order(13)
	@DisplayName("Should successfully register multiple users")
	void registerMultipleUsers() throws Exception {
		User user1 = new User(0, "Anna", "Smith", LocalDate.of(1985, 7, 22), User.Gender.FEMALE,
				"anna.smith.test@email.de", "Secure456", false);
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

		userRepository.deleteUser(registered1.userID());
		userRepository.deleteUser(registered2.userID());
		userRepository.deleteUser(registered3.userID());
	}

	@Test
	@Order(14)
	@DisplayName("Should handle all gender enum values correctly")
	void testAllGenders() throws Exception {
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

		userRepository.deleteUser(registered1.userID());
		userRepository.deleteUser(registered2.userID());
		userRepository.deleteUser(registered3.userID());
	}
}
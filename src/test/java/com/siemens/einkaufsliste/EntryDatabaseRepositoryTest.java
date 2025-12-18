package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;
import com.siemens.einkaufsliste.database.repository.DataAccessException;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.EntryRepository;
import com.siemens.einkaufsliste.database.repository.ProductRepository;
import com.siemens.einkaufsliste.database.repository.UserRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class EntryDatabaseRepositoryTest {

	private static EntryRepository entryRepository;
	private static UserRepository userRepository;
	private static ProductRepository productRepository;

	private static User testUser;
	private static Product testProduct;

	@BeforeAll
	static void setupDatabase() throws Exception {
		Database.connect();
		entryRepository = Database.getEntries();
		userRepository = Database.getUsers();
		productRepository = Database.getProducts();
	}

	@AfterAll
	static void teardownDatabase() {
		Database.disconnect();
	}

	@BeforeEach
	void setupTests() throws Exception {
		testUser = createUser();
		testProduct = createProduct();
	}

	@AfterEach
	void teardownTests() throws Exception {
		if (testUser != null) {
			userRepository.deleteUser(testUser.userID());
			testUser = null;
		}
		if (testProduct != null) {
			productRepository.removeProduct(testProduct.productID());
			testProduct = null;
		}
	}

	@Test
	@Order(1)
	@DisplayName("Should assign positive ID to new entry")
	void negativeEntryIdBecomesPositive() throws Exception {
		Entry testEntry = new Entry(-1, testUser.userID(), testProduct.productID(), 2, null);
		Entry newEntry = entryRepository.addEntry(testEntry);
		assertTrue(newEntry.entryID() > 0);
		entryRepository.removeEntry(newEntry.entryID());
	}

	@Test
	@Order(2)
	@DisplayName("Should transfer entry data correctly on creation")
	void addedEntryIsTransferedCorrectly() throws Exception {
		Entry testEntry = new Entry(-1, testUser.userID(), testProduct.productID(), 420, null);
		Entry newEntry = entryRepository.addEntry(testEntry);

		assertTrue(testEntry.userID() == newEntry.userID());
		assertTrue(testEntry.productID() == newEntry.productID());
		assertTrue(testEntry.quantity() == newEntry.quantity());

		if (testEntry.checkDate() == null || newEntry.checkDate() == null) {
			assertTrue(testEntry.checkDate() == null && newEntry.checkDate() == null);
		} else {
			assertTrue(testEntry.checkDate().isEqual(newEntry.checkDate()));
		}
		entryRepository.removeEntry(newEntry.entryID());
	}

	@Test
	@Order(3)
	@DisplayName("Should retrieve entry by ID")
	void getEntryById() throws Exception {
		Entry testEntry = new Entry(-1, testUser.userID(), testProduct.productID(), 420, null);
		Entry newEntry = entryRepository.addEntry(testEntry);

		Optional<Entry> getEntry = entryRepository.getEntry(newEntry.entryID());
		if (getEntry.isPresent()) {
			assertTrue(newEntry.equals(getEntry.get()));
		}
		entryRepository.removeEntry(newEntry.entryID());
	}

	@Test
	@Order(4)
	@DisplayName("Should update entry quantity")
	void updateQuantity() throws Exception {
		Entry entry = entryRepository.addEntry(new Entry(-1, testUser.userID(), testProduct.productID(), 1, null));
		int newQuantity = 99;
		Entry updated = entryRepository.updateQuantity(entry.entryID(), newQuantity);
		assertTrue(updated.quantity() == newQuantity);
		entryRepository.removeEntry(entry.entryID());
	}

	@Test
	@Order(5)
	@DisplayName("Should handle checking and unchecking of entries")
	void uncheckEntry() throws Exception {
		Entry entry = entryRepository.addEntry(new Entry(-1, testUser.userID(), testProduct.productID(), 3, null));

		Entry checked = entryRepository.checkEntry(entry.entryID());
		assertTrue(checked.checkDate() != null);

		Entry unchecked = entryRepository.uncheckEntry(entry.entryID());
		assertTrue(unchecked.checkDate() == null);

		entryRepository.removeEntry(entry.entryID());
	}

	@Test
	@Order(6)
	@DisplayName("Should remove entry successfully")
	void removeEntry() throws Exception {
		Entry entry = entryRepository.addEntry(new Entry(-1, testUser.userID(), testProduct.productID(), 4, null));
		entryRepository.removeEntry(entry.entryID());
		Optional<Entry> result = entryRepository.getEntry(entry.entryID());
		assertTrue(result.isEmpty());
	}

	@Test
	@Order(7)
	@DisplayName("Should return empty optional for non-existent entry")
	void getEntryNotFound() throws Exception {
		Optional<Entry> result = entryRepository.getEntry(-99999);
		assertTrue(result.isEmpty());
	}

	@Test
	@Order(8)
	@DisplayName("Should silently fail when removing non-existent entry")
	void removeEntryNotExisting() throws Exception {
		entryRepository.removeEntry(-99999);
	}

	@Test
	@Order(9)
	@DisplayName("Should retrieve entries filtered by User ID correctly")
	void getEntriesByUserId() throws Exception {
		List<User> testUsers = new ArrayList<>();
		List<Product> testProducts = new ArrayList<>();
		long unique = System.currentTimeMillis();

		testUsers.add(userRepository.registerUser(new User(-1, "Quentin", "Tarantino", LocalDate.of(1963, 3, 27),
				Gender.MALE, "quentin.tarantino+test" + unique + "@email.com", "QT63", true)));
		testUsers.add(userRepository.registerUser(new User(-1, "Aiko", "Tanaka", LocalDate.of(1990, 8, 15),
				Gender.FEMALE, "aiko.tanaka+test" + unique + "@email.com", "AT90", true)));
		testUsers.add(userRepository.registerUser(new User(-1, "Lars", "Magnusson", LocalDate.of(1982, 12, 5),
				Gender.MALE, "lars.magnusson+test" + unique + "@email.com", "LM82", true)));
		testUsers.add(userRepository.registerUser(new User(-1, "Fatima", "Alami", LocalDate.of(1988, 6, 21),
				Gender.FEMALE, "fatima.alami+test" + unique + "@email.com", "FA88", true)));
		testUsers.add(userRepository.registerUser(new User(-1, "Mateo", "Silva", LocalDate.of(1995, 11, 2), Gender.MALE,
				"mateo.silva+test" + unique + "@email.com", "MS95", true)));

		testProducts.add(productRepository.addProduct(new Product(-1, "Spezi Zero", Category.DRINKS, "Paulaner", 109)));
		testProducts.add(productRepository.addProduct(new Product(-1, "Milch", Category.MILK, "Weihenstephan", 129)));
		testProducts.add(productRepository.addProduct(new Product(-1, "Brot", Category.WHEAT, "Bäcker", 249)));
		testProducts.add(productRepository.addProduct(new Product(-1, "Käse", Category.MILK, "Alpenhain", 299)));
		testProducts.add(productRepository.addProduct(new Product(-1, "Apfel", Category.FRUITS, "Obsthof", 59)));

		List<Entry> createdEntries = new ArrayList<>();
		for (int i = 0; i < testUsers.size(); i++) {
			Entry entry = entryRepository
					.addEntry(new Entry(-1, testUsers.get(i).userID(), testProducts.get(i).productID(), i + 1, null));
			createdEntries.add(entry);
		}

		for (int i = 0; i < testUsers.size(); i++) {
			List<Entry> entries = entryRepository.getEntries(testUsers.get(i).userID());
			int expectedEntryId = createdEntries.get(i).entryID();
			boolean found = false;
			for (Entry e : entries) {
				if (e.entryID() == expectedEntryId) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}

		for (Entry entry : createdEntries) {
			entryRepository.removeEntry(entry.entryID());
		}
		for (User user : testUsers) {
			userRepository.deleteUser(user.userID());
		}
		for (Product product : testProducts) {
			productRepository.removeProduct(product.productID());
		}
	}

	private User createUser() throws DataAccessException {
		long unique = System.nanoTime();
		return userRepository.registerUser(new User(-1, "Test", "User", LocalDate.of(1990, 1, 1), Gender.MALE,
				"test.user" + unique + "@email.com", "PAWO", true));
	}

	private Product createProduct() throws DataAccessException {
		long unique = System.nanoTime();
		return productRepository
				.addProduct(new Product(-1, "Testprodukt" + unique, Category.DRINKS, "Testhersteller", 100));
	}
}
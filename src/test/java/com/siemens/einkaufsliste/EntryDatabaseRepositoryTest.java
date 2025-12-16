package com.siemens.einkaufsliste;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.EntryRepository;
import com.siemens.einkaufsliste.database.repository.ProductRepository;
import com.siemens.einkaufsliste.database.repository.UserRepository;

public final class EntryDatabaseRepositoryTest {
    private static EntryRepository entryRepository;
    private static UserRepository userRepository;
    private static ProductRepository productRepository;

    private static final List<User> testUsers = new ArrayList<>();
    private static final List<Product> testProducts = new ArrayList<>();

    @BeforeAll
    static void setupDatabase() {
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
    void setupTests() {
        createTestData();
    }

    @AfterEach
    void teardownTests() {
        cleanUpTestData();
    }

    @Test
    @DisplayName("negative EntryId becomes positive")
    void negativeEntryIdBecomesPositive(){

        Entry testEntry = new Entry(-1, testUsers.get(0).userID(), testProducts.get(0).productID(), 2, null);
        Entry newEntry = entryRepository.addEntry(testEntry);
        assertTrue(newEntry.entryID()>0);
        entryRepository.removeEntry(newEntry.entryID());

    }

    @Test
    @DisplayName("added Entry is transfered correctly")
    void addedEntryIsTransferedCorrectly() {
        Entry testEntry = new Entry(-1, testUsers.get(0).userID(), testProducts.get(0).productID(), 420, null);
        Entry newEntry = entryRepository.addEntry(testEntry);
        
        assertTrue(testEntry.userID() == newEntry.userID());
        assertTrue(testEntry.productID() == newEntry.productID());
        assertTrue(testEntry.quantity() == newEntry.quantity());
        if (testEntry.checkDate() == null || newEntry.checkDate() == null)
            assertTrue(testEntry.checkDate() == null && newEntry.checkDate() == null);
        else 
            assertTrue(testEntry.checkDate().isEqual(newEntry.checkDate()));
        entryRepository.removeEntry(newEntry.entryID());
    }
    @Test
    @DisplayName("addEntryWithDateNotNullThrowsError")
    void addEntryWithDateNotNullThrowsError(){
        assertThrows(IllegalArgumentException.class, () -> {
            Entry testEntry = new Entry(-1, testUsers.get(0).userID(), testProducts.get(0).productID(), 200, LocalDate.now());
            Entry newEntry = entryRepository.addEntry(testEntry);
        });
    }

    @Test
    @DisplayName("getEntryById")
    void getEntryById(){
        Entry testEntry = new Entry(-1, testUsers.get(0).userID(), testProducts.get(0).productID(), 420, null);
        Entry newEntry = entryRepository.addEntry(testEntry);
        
        Optional<Entry> getEntry = entryRepository.getEntry(newEntry.entryID());
        if (getEntry.isPresent())
            assertTrue(newEntry.equals(getEntry.get()));
    }
    @Test
    @DisplayName("updateQuantity")
    void updateQuantity() {
        Entry entry = entryRepository.addEntry(new Entry(-1, testUsers.get(0).userID(), testProducts.get(0).productID(), 1, null));
        int newQuantity = 99;
        Entry updated = entryRepository.updateQuantity(entry.entryID(), newQuantity);
        assertTrue(updated.quantity() == newQuantity);
        entryRepository.removeEntry(entry.entryID());
    }

    @Test
    @DisplayName("checkUncheckEntry")
    void uncheckEntry() {
        Entry entry = entryRepository.addEntry(new Entry(-1, testUsers.get(2).userID(), testProducts.get(2).productID(), 3, null));
        Entry checked = entryRepository.checkEntry(entry.entryID());
        assertTrue(checked.checkDate() == null);
        Entry unchecked = entryRepository.uncheckEntry(entry.entryID());
        assertTrue(unchecked.checkDate() == null);
        entryRepository.removeEntry(entry.entryID());
    }

    @Test
    @DisplayName("removeEntry")
    void removeEntry() {
        Entry entry = entryRepository.addEntry(new Entry(-1, testUsers.get(3).userID(), testProducts.get(3).productID(), 4, null));
        entryRepository.removeEntry(entry.entryID());
        Optional<Entry> result = entryRepository.getEntry(entry.entryID());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getEntryNotFound")
    void getEntryNotFound() {
        Optional<Entry> result = entryRepository.getEntry(-99999);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("removeEntryNotExisting")
    void removeEntryNotExisting() {
        // Sollte keine Exception werfen
        entryRepository.removeEntry(-99999);
    }

    @Test
    @DisplayName("getEntriesByUserId")
    void getEntriesByUserId() {
        // Für jeden User einen Entry anlegen
        List<Entry> createdEntries = new ArrayList<>();
        for (int i = 0; i < testUsers.size(); i++) {
            Entry entry = entryRepository.addEntry(new Entry(-1, testUsers.get(i).userID(), testProducts.get(i).productID(), i + 1, null));
            createdEntries.add(entry);
        }
        // Prüfen, ob für jeden User genau ein Entry gefunden wird
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
        // Aufräumen
        for (Entry entry : createdEntries) {
            entryRepository.removeEntry(entry.entryID());
        }
    }

    /**
     * Sets private testUsers and private testProducts (je 5)
     */
    void createTestData() {
        testUsers.clear();
        testProducts.clear();
        long unique = System.currentTimeMillis();
        testUsers.add(userRepository.registerUser(new User(-1, "Quentin", "Tarantino", LocalDate.of(1963, 3, 27), Gender.MALE, "quentin.tarantino+test"+unique+"@email.com", "QT63", true)));
        testUsers.add(userRepository.registerUser(new User(-1, "Aiko", "Tanaka", LocalDate.of(1990, 8, 15), Gender.FEMALE, "aiko.tanaka+test"+unique+"@email.com", "AT90", true)));
        testUsers.add(userRepository.registerUser(new User(-1, "Lars", "Magnusson", LocalDate.of(1982, 12, 5), Gender.MALE, "lars.magnusson+test"+unique+"@email.com", "LM82", true)));
        testUsers.add(userRepository.registerUser(new User(-1, "Fatima", "Alami", LocalDate.of(1988, 6, 21), Gender.FEMALE, "fatima.alami+test"+unique+"@email.com", "FA88", true)));
        testUsers.add(userRepository.registerUser(new User(-1, "Mateo", "Silva", LocalDate.of(1995, 11, 2), Gender.MALE, "mateo.silva+test"+unique+"@email.com", "MS95", true)));

        testProducts.add(productRepository.addProduct(new Product(-1, "Spezi Zero", Category.DRINKS, "Paulaner", 109)));
        testProducts.add(productRepository.addProduct(new Product(-1, "Milch", Category.MILK, "Weihenstephan", 129)));
        testProducts.add(productRepository.addProduct(new Product(-1, "Brot", Category.WHEAT, "Bäcker", 249)));
        testProducts.add(productRepository.addProduct(new Product(-1, "Käse", Category.MILK, "Alpenhain", 299)));
        testProducts.add(productRepository.addProduct(new Product(-1, "Apfel", Category.FRUITS, "Obsthof", 59)));
    }

    /**
     * Löscht alle Testdaten aus der Datenbank
     */
    void cleanUpTestData() {
        for (User user : testUsers) {
            userRepository.deleteUser(user.userID());
        }
        testUsers.clear();

        for (Product product : testProducts) {
            productRepository.removeProduct(product.productID());
        }
        testProducts.clear();
    }
}

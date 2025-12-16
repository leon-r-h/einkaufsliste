package com.siemens.einkaufsliste;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.Product.Category;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.ProductRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class ProductDatabaseRepositoryTest {

    private static ProductRepository productRepository;
    private static Product testProduct;

    @BeforeAll
    static void setupDatabase() {
        Database.connect();
        productRepository = Database.getProducts(); 
        }

    @AfterAll
    static void teardownDatabase() {
        Database.disconnect();
    }

    @Test
    @Order(1)
    @DisplayName("Add Test Product - Success")
    void addTestProduct() {
        Product inputProduct = new Product(0, "Test Apfel", Category.FRUITS, "BioMarke", 299);

        testProduct = productRepository.addProduct(inputProduct);

        assertNotNull(testProduct, "Das zurückgegebene Produkt darf nicht null sein");
        assertNotEquals(0, testProduct.productID(), "Die ID muss von der Datenbank generiert worden sein");
        assertEquals("Test Apfel", testProduct.name());
    }

    @Test
    @Order(2)
    @DisplayName("Get All Products - Success")
    void getAllProducts() {
        List<Product> products = productRepository.getProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty(), "Produktliste sollte nicht leer sein");
        
        boolean found = products.stream()
                .anyMatch(p -> p.productID() == testProduct.productID());
        
        assertTrue(found, "Das hinzugefügte Produkt (ID " + testProduct.productID() + ") wurde nicht gefunden");
    }

    @Test
    @Order(3)
    @DisplayName("Get Product by ID - Success")
    void getProductById() {
        Optional<Product> foundProduct = productRepository.getProduct(testProduct.productID());

        assertTrue(foundProduct.isPresent(), "Produkt mit ID " + testProduct.productID() + " sollte gefunden werden");
        assertEquals(testProduct.name(), foundProduct.get().name());
        assertEquals(testProduct.price(), foundProduct.get().price());
    }

    @Test
    @Order(4)
    @DisplayName("Get Product by ID - Not Found")
    void getProductByIdNotFound() {
        Optional<Product> foundProduct = productRepository.getProduct(999999);
        assertFalse(foundProduct.isPresent(), "Produkt sollte nicht gefunden werden");
    }

    @Test
    @Order(5)
    @DisplayName("Add Multiple Products - Different Categories")
    void addMultipleProducts() {
        Product p1 = new Product(0, "Test Weizenbrot", Category.WHEAT, "BäckerMeister", 249);
        Product p2 = new Product(0, "Test Laptop", Category.ELECTRONICS, "TechBrand", 89999);
        Product p3 = new Product(0, "Test Milch", Category.MILK, "AlpenMilch", 129);
        Product p4 = new Product(0, "Test Cola", Category.DRINKS, "CocaCola", 199);

        Product saved1 = productRepository.addProduct(p1);
        Product saved2 = productRepository.addProduct(p2);
        Product saved3 = productRepository.addProduct(p3);
        Product saved4 = productRepository.addProduct(p4);
        
        assertNotNull(saved1);
        assertNotNull(saved2);
        assertNotNull(saved3);
        assertNotNull(saved4);

        List<Product> products = productRepository.getProducts();
        assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Weizenbrot")));
        assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Milch")));

        // Cleanup mit den ECHTEN IDs
        productRepository.removeProduct(saved1.productID());
        productRepository.removeProduct(saved2.productID());
        productRepository.removeProduct(saved3.productID());
        productRepository.removeProduct(saved4.productID());
    }

    @Test
    @Order(6)
    @DisplayName("Test All Categories")
    void testAllCategories() {
        Product wheat = new Product(0, "Test Wheat", Category.WHEAT, "Brand1", 100);
        Product electronics = new Product(0, "Test Electronics", Category.ELECTRONICS, "Brand2", 200);
        
        Product s1 = productRepository.addProduct(wheat);
        Product s2 = productRepository.addProduct(electronics);

        List<Product> products = productRepository.getProducts();
        
        assertTrue(products.stream().anyMatch(p -> p.category() == Category.WHEAT));
        assertTrue(products.stream().anyMatch(p -> p.category() == Category.ELECTRONICS));

        if(s1 != null) productRepository.removeProduct(s1.productID());
        if(s2 != null) productRepository.removeProduct(s2.productID());
    }

    @Test
    @Order(7)
    @DisplayName("Add Product with Zero Price")
    void addProductWithZeroPrice() {
        Product freeProduct = new Product(0, "Test Gratis", Category.SNACKS, "FreeBrand", 0);

        Product saved = productRepository.addProduct(freeProduct);
        assertNotNull(saved);
        assertEquals(0, saved.price());

        productRepository.removeProduct(saved.productID());
    }

    @Test
    @Order(8)
    @DisplayName("Add Product with High Price")
    void addProductWithHighPrice() {
        Product expensiveProduct = new Product(0, "Test Luxus", Category.ELECTRONICS, "LuxusBrand", 999999);

        Product saved = productRepository.addProduct(expensiveProduct);
        
        assertNotNull(saved, "Gespeichertes Produkt darf nicht null sein");
        assertEquals(999999, saved.price());
        
        Optional<Product> found = productRepository.getProduct(saved.productID());
        assertTrue(found.isPresent());
        assertEquals(999999, found.get().price());

        productRepository.removeProduct(saved.productID());
    }

    @Test
    @Order(9)
    @DisplayName("Remove Product - Success")
    void removeProduct() {
        assertNotEquals(0, testProduct.productID(), "Testprodukt sollte eine valide ID haben");
        
        Optional<Product> beforeRemoval = productRepository.getProduct(testProduct.productID());
        assertTrue(beforeRemoval.isPresent(), "Produkt sollte vor dem Löschen existieren");

        productRepository.removeProduct(testProduct.productID());

        Optional<Product> afterRemoval = productRepository.getProduct(testProduct.productID());
        assertFalse(afterRemoval.isPresent(), "Gelöschtes Produkt sollte nicht mehr gefunden werden");
    }

    @Test
    @Order(10)
    @DisplayName("Remove Product - Not Found")
    void removeProductNotFound() {
        assertDoesNotThrow(() -> {
            productRepository.removeProduct(999999);
        });
    }

    @Test
    @Order(11)
    @DisplayName("Add Product with Empty Brand")
    void addProductWithEmptyBrand() {
        Product noBrandProduct = new Product(0, "Test No Brand", Category.VEGETABLES, "", 150);

        Product saved = productRepository.addProduct(noBrandProduct);
        assertNotNull(saved);
        assertEquals("", saved.brand());

        productRepository.removeProduct(saved.productID());
    }
    
    @Test
	@Order(12) 
	@DisplayName("Find Products by Category - Filter & Completeness")
	void findProductsByCategory() {
		
		Category searchCategory = Category.FRUITS;
		
		Category noiseCategory = Category.ELECTRONICS;

		Product target1 = new Product(0, "Such-Apfel", searchCategory, "BioHof", 199);
		Product target2 = new Product(0, "Such-Banane", searchCategory, "Chiquita", 299);
		Product noise1 = new Product(0, "Stör-Laptop", noiseCategory, "Dell", 99999);

		Product savedTarget1 = productRepository.addProduct(target1);
		Product savedTarget2 = productRepository.addProduct(target2);
		Product savedNoise1 = productRepository.addProduct(noise1);

		assertNotNull(savedTarget1);
		assertNotNull(savedTarget2);
		assertNotNull(savedNoise1);

		List<Product> foundProducts = productRepository.findProducts(searchCategory);

		
		assertNotNull(foundProducts, "Die Rückgabeliste darf nicht null sein");
		assertFalse(foundProducts.isEmpty(), "Es sollten Produkte gefunden werden");

		boolean allMatchCategory = foundProducts.stream()
				.allMatch(p -> p.category() == searchCategory);
		assertTrue(allMatchCategory, "Alle gefundenen Produkte müssen die Kategorie " + searchCategory + " haben");

		boolean containsTarget1 = foundProducts.stream()
				.anyMatch(p -> p.productID() == savedTarget1.productID());
		boolean containsTarget2 = foundProducts.stream()
				.anyMatch(p -> p.productID() == savedTarget2.productID());
		
		assertTrue(containsTarget1, "Der Such-Apfel sollte gefunden werden");
		assertTrue(containsTarget2, "Die Such-Banane sollte gefunden werden");

		boolean containsNoise = foundProducts.stream()
				.anyMatch(p -> p.productID() == savedNoise1.productID());
		
		assertFalse(containsNoise, "Der Stör-Laptop (Electronics) darf NICHT gefunden werden");

		productRepository.removeProduct(savedTarget1.productID());
		productRepository.removeProduct(savedTarget2.productID());
		productRepository.removeProduct(savedNoise1.productID());
	}
    
    
    
}
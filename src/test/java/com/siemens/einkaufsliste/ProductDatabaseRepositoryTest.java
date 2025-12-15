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
	@Order(2)
	@DisplayName("Get All Products - Success")
	void getAllProducts() {
		List<Product> products = productRepository.getProducts();

		assertNotNull(products);
		assertFalse(products.isEmpty(), "Produktliste sollte nicht leer sein");
		assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Apfel")));
	}

	@Test
	@Order(3)
	@DisplayName("Get Product by ID - Success")
	void getProductById() {
		Optional<Product> foundProduct = productRepository.getProduct(testProduct.productID());

		assertTrue(foundProduct.isPresent(), "Produkt sollte gefunden werden");
		assertEquals(testProduct.name(), foundProduct.get().name());
		assertEquals(testProduct.category(), foundProduct.get().category());
		assertEquals(testProduct.brand(), foundProduct.get().brand());
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
		Product product1 = new Product(1, "Test Weizenbrot (Alkoholisch, aber Glutenfrei. Und ohne Zucker. Aber mit Mehl)", Category.ALCOHOL, "BäckerMeister", 249);
		Product product2 = new Product(2, "Test Laptop", Category.ELECTRONICS, "TechBrand", 89999);
		Product product3 = new Product(3, "Test Milch", Category.ALCOHOL, "AlpenMilch", 129);
		Product product4 = new Product(4, "Test Cola", Category.DRINKS, "CocaCola", 199);

		assertDoesNotThrow(() -> {
			productRepository.addProduct(product1);
			productRepository.addProduct(product2);
			productRepository.addProduct(product3);
			productRepository.addProduct(product4);
		});

		List<Product> products = productRepository.getProducts();

		assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Weizenbrot (Alkoholisch, aber Glutenfrei. Und ohne Zucker. Aber mit Mehl)")));
		assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Laptop")));
		assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Milch")));
		assertTrue(products.stream().anyMatch(p -> p.name().equals("Test Cola")));

		// Cleanup
		products.stream()
				.filter(p -> p.name().startsWith("Test ") && !p.name().equals("Test Apfel"))
				.forEach(p -> productRepository.removeProduct(p.productID()));
		
		productRepository.addProduct(product1);
	}

	
	@Test
	@Order(6)
	@DisplayName("Test All Categories")
	void testAllCategories() {
		Product wheat = new Product(0, "Test Wheat", Category.WHEAT, "Brand1", 100);
		Product electronics = new Product(0, "Test Electronics", Category.ELECTRONICS, "Brand2", 200);
		Product fruits = new Product(0, "Test Fruits", Category.FRUITS, "Brand3", 300);
		Product vegetables = new Product(0, "Test Vegetables", Category.VEGETABLES, "Brand4", 400);
		Product milk = new Product(0, "Test Milk", Category.MILK, "Brand5", 500);
		Product drinks = new Product(0, "Test Drinks", Category.DRINKS, "Brand6", 600);
		Product alcohol = new Product(0, "Test Alcohol", Category.ALCOHOL, "Brand7", 700);
		Product snacks = new Product(0, "Test Snacks", Category.SNACKS, "Brand8", 800);
		Product household = new Product(0, "Test Household", Category.HOUSEHOLD, "Brand9", 900);

		assertDoesNotThrow(() -> {
			productRepository.addProduct(wheat);
			productRepository.addProduct(electronics);
			productRepository.addProduct(fruits);
			productRepository.addProduct(vegetables);
			productRepository.addProduct(milk);
			productRepository.addProduct(drinks);
			productRepository.addProduct(alcohol);
			productRepository.addProduct(snacks);
			productRepository.addProduct(household);
		});

		List<Product> products = productRepository.getProducts();
		
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.WHEAT));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.ELECTRONICS));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.FRUITS));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.VEGETABLES));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.MILK));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.DRINKS));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.ALCOHOL));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.SNACKS));
		assertTrue(products.stream().anyMatch(p -> p.category() == Category.HOUSEHOLD));

		// Cleanup
		products.stream()
				.filter(p -> p.name().startsWith("Test ") && !p.name().equals("Test Apfel"))
				.forEach(p -> productRepository.removeProduct(p.productID()));
	}

	@Test
	@Order(7)
	@DisplayName("Add Product with Zero Price")
	void addProductWithZeroPrice() {
		Product freeProduct = new Product(0, "Test Gratis", Category.SNACKS, "FreeBrand", 0);

		assertDoesNotThrow(() -> {
			productRepository.addProduct(freeProduct);
		});

		List<Product> products = productRepository.getProducts();
		Optional<Product> found = products.stream()
				.filter(p -> p.name().equals("Test Gratis"))
				.findFirst();

		assertTrue(found.isPresent());
		assertEquals(0, found.get().price());

		// Cleanup
		productRepository.removeProduct(found.get().productID());
	}

	@Test
	@Order(8)
	@DisplayName("Add Product with High Price")
	void addProductWithHighPrice() {
		Product expensiveProduct = new Product(0, "Test Luxus", Category.ELECTRONICS, "LuxusBrand", 999999);

		assertDoesNotThrow(() -> {
			productRepository.addProduct(expensiveProduct);
		});

		List<Product> products = productRepository.getProducts();
		Optional<Product> found = products.stream()
				.filter(p -> p.name().equals("Test Luxus"))
				.findFirst();

		assertTrue(found.isPresent());
		assertEquals(999999, found.get().price());

		// Cleanup
		productRepository.removeProduct(found.get().productID());
	}

	@Test
	@Order(9)
	@DisplayName("Remove Product - Success")
	void removeProduct() {
		// First verify the product exists
		Optional<Product> beforeRemoval = productRepository.getProduct(testProduct.productID());
		assertTrue(beforeRemoval.isPresent(), "Produkt sollte vor dem Löschen existieren");

		assertDoesNotThrow(() -> {
			productRepository.removeProduct(testProduct.productID());
		});

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

		assertDoesNotThrow(() -> {
			productRepository.addProduct(noBrandProduct);
		});

		List<Product> products = productRepository.getProducts();
		Optional<Product> found = products.stream()
				.filter(p -> p.name().equals("Test No Brand"))
				.findFirst();

		assertTrue(found.isPresent());
		assertEquals("", found.get().brand());

		// Cleanup
		productRepository.removeProduct(found.get().productID());
	}

	@Test
	@Order(12)
	@DisplayName("Add Products with Same Name Different Categories")
	void addProductsSameNameDifferentCategories() {
		Product product1 = new Product(0, "Test Duplicate", Category.FRUITS, "Brand1", 100);
		Product product2 = new Product(0, "Test Duplicate", Category.VEGETABLES, "Brand2", 200);

		assertDoesNotThrow(() -> {
			productRepository.addProduct(product1);
			productRepository.addProduct(product2);
		});

		List<Product> products = productRepository.getProducts();
		long count = products.stream()
				.filter(p -> p.name().equals("Test Duplicate"))
				.count();

		assertEquals(2, count, "Beide Produkte mit gleichem Namen sollten existieren");

		// Cleanup
		products.stream()
				.filter(p -> p.name().equals("Test Duplicate"))
				.forEach(p -> productRepository.removeProduct(p.productID()));
	}

	@Test
	@Order(13)
	@DisplayName("Verify Product Record Immutability")
	void verifyProductImmutability() {
		Product original = new Product(1, "Original", Category.FRUITS, "Brand", 100);
		
		// Records are immutable, so we can only verify the values
		assertEquals(1, original.productID());
		assertEquals("Original", original.name());
		assertEquals(Category.FRUITS, original.category());
		assertEquals("Brand", original.brand());
		assertEquals(100, original.price());
	}
}
package com.siemens.einkaufsliste.database.repository;

import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.DataAccessException;
import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;
import com.siemens.einkaufsliste.database.model.ShoppingListItem;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EntryUtil {

	public static void main(String[] args) {
        try {
			// Database connecten
			Database.connect();
			System.out.println("Database connected.");

            // Repositories holen
            UserRepository userRepository = Database.getUsers();
            ProductRepository productRepository = Database.getProducts();
            EntryRepository entryRepository = Database.getEntries();

            // Listen für erstellte IDs zum späteren Löschen
            List<Integer> createdUserIds = new ArrayList<>();
            List<Integer> createdProductIds = new ArrayList<>();
            List<Integer> createdEntryIds = new ArrayList<>();

            System.out.println("=== Testdaten erstellen ===");

            // User erstellen (ohne Passwort, nur für Test)
            User user1 = new User(0, "Max", "Mustermann", LocalDate.now(), Gender.MALE, "max@example.com", "password123", false);
            User user2 = new User(0, "Anna", "Schmidt", LocalDate.now(), Gender.FEMALE, "anna@example.com", "password456", true);
            
            User registeredUser1 = userRepository.registerUser(user1);
            User registeredUser2 = userRepository.registerUser(user2);
            createdUserIds.add(registeredUser1.userID());
            createdUserIds.add(registeredUser2.userID());
            
            System.out.println("User erstellt: " + registeredUser1.userID() + ", " + registeredUser2.userID());

            // Produkte erstellen (mit allen Feldern: productID, name, category, brand, price)
            Product product1 = new Product(0, "Milch", Product.Category.MILK, "Alpenmilch", 129); // 1,29 EUR
            Product product2 = new Product(0, "Brot", Product.Category.WHEAT, "Schäfer", 249); // 2,49 EUR
            Product product3 = new Product(0, "Käse", Product.Category.MILK, "Bergkäse", 399); // 3,99 EUR
            Product product4 = new Product(0, "Äpfel", Product.Category.FRUITS, "BioFrisch", 179); // 1,79 EUR

            Product savedProduct1 = productRepository.addProduct(product1);
            Product savedProduct2 = productRepository.addProduct(product2);
            Product savedProduct3 = productRepository.addProduct(product3);
            Product savedProduct4 = productRepository.addProduct(product4);
            
            createdProductIds.add(savedProduct1.productID());
            createdProductIds.add(savedProduct2.productID());
            createdProductIds.add(savedProduct3.productID());
            createdProductIds.add(savedProduct4.productID());

            System.out.println("Produkte erstellt: " + savedProduct1.productID() + ", " + savedProduct2.productID() + ", " + savedProduct3.productID() + ", " + savedProduct4.productID());

            // Entries für User 1 erstellen (mit LocalDate statt LocalDateTime)
            Entry entry1 = new Entry(0, registeredUser1.userID(), savedProduct1.productID(), 2, LocalDate.now().minusDays(1));
            Entry entry2 = new Entry(0, registeredUser1.userID(), savedProduct2.productID(), 1, LocalDate.now());
            Entry entry3 = new Entry(0, registeredUser1.userID(), savedProduct3.productID(), 3, null);

            Entry savedEntry1 = entryRepository.addEntry(entry1);
            Entry savedEntry2 = entryRepository.addEntry(entry2);
            Entry savedEntry3 = entryRepository.addEntry(entry3);
            
            createdEntryIds.add(savedEntry1.entryID());
            createdEntryIds.add(savedEntry2.entryID());
            createdEntryIds.add(savedEntry3.entryID());

            // Entries für User 2 erstellen
            Entry entry4 = new Entry(0, registeredUser2.userID(), savedProduct2.productID(), 2, LocalDate.now().minusDays(2));
            Entry entry5 = new Entry(0, registeredUser2.userID(), savedProduct4.productID(), 5, LocalDate.now());

            Entry savedEntry4 = entryRepository.addEntry(entry4);
            Entry savedEntry5 = entryRepository.addEntry(entry5);
            
            createdEntryIds.add(savedEntry4.entryID());
            createdEntryIds.add(savedEntry5.entryID());

            System.out.println("Entries erstellt: " + createdEntryIds.size() + " Einträge");

            // Export für User 1
            System.out.println("\n=== Export für User " + registeredUser1.userID() + " ===");
            exportEntriesAsCsv(registeredUser1.userID());
            System.out.println("CSV-Export erfolgreich für User " + registeredUser1.userID());
            
            exportEntriesAsPdf(registeredUser1.userID());
            System.out.println("PDF-Export erfolgreich für User " + registeredUser1.userID());

            // Export für User 2
            System.out.println("\n=== Export für User " + registeredUser2.userID() + " ===");
            exportEntriesAsCsv(registeredUser2.userID());
            System.out.println("CSV-Export erfolgreich für User " + registeredUser2.userID());
            
            exportEntriesAsPdf(registeredUser2.userID());
            System.out.println("PDF-Export erfolgreich für User " + registeredUser2.userID());

            // Alle erstellten Einträge löschen
            System.out.println("\n=== Testdaten löschen ===");
            
            for (int entryId : createdEntryIds) {
                entryRepository.removeEntry(entryId);
            }
            System.out.println("Entries gelöscht: " + createdEntryIds.size());

            for (int productId : createdProductIds) {
                productRepository.removeProduct(productId);
            }
            System.out.println("Produkte gelöscht: " + createdProductIds.size());

            for (int userId : createdUserIds) {
                userRepository.deleteUser(userId);
            }
            System.out.println("User gelöscht: " + createdUserIds.size());

			Database.disconnect();
			System.out.println("Database disconnected");


            System.out.println("\n=== Test abgeschlossen ===");

        } catch (IOException | DataAccessException | IllegalArgumentException e) {
            System.err.println("Fehler beim Test: " + e.getMessage());
            e.printStackTrace();
        }
    }

	public static void exportEntriesAsCsv(int userID) throws IOException, DataAccessException {
		EntryRepository entryRepository = Database.getEntries();
		List<ShoppingListItem> items = entryRepository.getEntries(userID);
		File folder = new File("entry_exports/" + userID + "/csv");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File("entry_exports/" + userID + "/csv/entries_user_" + userID + "_"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".csv");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write("Product;Quantity;Price;CheckedDate\n");
			for (ShoppingListItem sli : items) {
				if (sli.entry().checkDate() != null) {
					writer.write(sli.product().name() + ";" + sli.entry().quantity() + ";" + sli.product().price() + ";" + sli.entry().checkDate());
				} else {
					writer.write(sli.product().name() + ";" + sli.entry().quantity() + ";" + sli.product().price() + ";" + "-");
				}
				writer.write("\n");
			}
		}
	}

	/**
	 *
	 * @param userID The ID of the user whose entries are to be loaded
	 * @return List of Entries loaded from the given file
	 * @throws IOException
	 * @throws DataAccessException
	 */

	public static void exportEntriesAsPdf(int userID) throws IOException, DataAccessException {
		EntryRepository entryRepository = Database.getEntries();
		UserRepository userRepository = Database.getUsers();
		List<ShoppingListItem> items = entryRepository.getEntries(userID);

		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
				float y = 750;

				// Überschrift
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
				content.beginText();
				content.newLineAtOffset(50, y);

				Optional<User> user = userRepository.getUser(userID);
				String userFirstname = "";
				String userLastname = "";
				if (user.isPresent()) {
					userFirstname = user.get().firstName();
					userLastname = user.get().lastName();
				} else {
					throw new IllegalArgumentException("User existiert nicht");
				}

				content.showText(userFirstname + " " + userLastname + "'s Formula Emendi");
				content.endText();

				y -= 20;

				content.beginText();
				content.newLineAtOffset(50, y);
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
				content.showText("effugere doloris");
				content.endText();

				y -= 40;

				// Tabellenkopf
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
				content.beginText();
				content.newLineAtOffset(50, y);
				content.showText("Produkt");
				content.endText();

				content.beginText();
				content.newLineAtOffset(250, y);
				content.showText("Menge");
				content.endText();

				content.beginText();
				content.newLineAtOffset(320, y);
				content.showText("Preis");
				content.endText();

				content.beginText();
				content.newLineAtOffset(420, y);
				content.showText("Datum");
				content.endText();

				y -= 25;

				// Einträge
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				int gesamtpreis = 0;

				for (ShoppingListItem sli : items) {
					Product product = sli.product();
					String produktName = product != null ? product.name() : "Unbekannt";
					String menge = String.valueOf(sli.entry().quantity());
					int preis = product != null ? product.price() : 0;
					String preisStr = String.format("%.2f EUR", preis / 100.0);
					gesamtpreis += preis;
					String datum = sli.entry().checkDate() != null ? sli.entry().checkDate().toString() : "-";

					content.beginText();
					content.newLineAtOffset(50, y);
					content.showText(produktName);
					content.endText();

					content.beginText();
					content.newLineAtOffset(250, y);
					content.showText(menge);
					content.endText();

					content.beginText();
					content.newLineAtOffset(320, y);
					content.showText(preisStr);
					content.endText();

					content.beginText();
					content.newLineAtOffset(420, y);
					content.showText(datum);
					content.endText();

					y -= 20;
					if (y < 100) {
						break; // Mehr Platz für Gesamtpreis
					}
				}

				// Gesamtpreis
				y -= 20;
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
				content.beginText();
				content.newLineAtOffset(50, y);
				content.showText("Gesamtpreis:");
				content.endText();

				content.beginText();
				content.newLineAtOffset(320, y);
				content.showText(String.format("%.2f EUR", gesamtpreis / 100.0));
				content.endText();
			}

			File folder = new File("entry_exports/" + userID + "/pdf/");
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File("entry_exports/" + userID + "/pdf/entries_user_" + userID + "_"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".pdf");
			doc.save(file);
		}
	}
}

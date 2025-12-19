package com.siemens.einkaufsliste.database.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.ShoppingListItem;
import com.siemens.einkaufsliste.database.model.User;

public final class EntryUtil {

	/**
	 *
	 * @param userID The ID of the user whose entries are to be loaded
	 * @param file   The target file where the CSV will be saved
	 * @return List of Entries loaded from the given file
	 * @throws IOException
	 * @throws DataAccessException
	 */
	public static void exportEntriesAsCsv(int userID, File file) throws IOException, DataAccessException {
		EntryRepository entryRepository = Database.getEntries();
		List<ShoppingListItem> items = entryRepository.getEntries(userID);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write("Product;Quantity;Price;CheckedDate\n");
			for (ShoppingListItem sli : items) {
				if (sli.entry().checkDate() != null) {
					writer.write(sli.product().name() + ";" + sli.entry().quantity() + ";" + sli.product().price() + ";"
							+ sli.entry().checkDate());
				} else {
					writer.write(sli.product().name() + ";" + sli.entry().quantity() + ";" + sli.product().price() + ";"
							+ "-");
				}
				writer.write("\n");
			}
		}
	}

	/**
	 *
	 * @param userID The ID of the user whose entries are to be loaded
	 * @param file   The target file where the PDF will be saved
	 * @return List of Entries loaded from the given file
	 * @throws IOException
	 * @throws DataAccessException
	 */

	public static void exportEntriesAsPdf(int userID, File file) throws IOException, DataAccessException {
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
				int gesamtpreis = entryRepository.totalPrice(userID);				

				for (ShoppingListItem sli : items) {
					Product product = sli.product();
					String produktName = product != null ? product.name() : "Unbekannt";
					String menge = String.valueOf(sli.entry().quantity());
					int preis = product != null ? product.price() : 0;
					String preisStr = String.format("%.2f EUR", preis / 100.0);
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

			doc.save(file);
		}
	}
}

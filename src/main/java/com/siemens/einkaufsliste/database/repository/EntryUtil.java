package com.siemens.einkaufsliste.database.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.model.User;

public final class EntryUtil {

    private EntryUtil() {}

    /* Testing Main Method */
    
    public static void main(String[] args) {

        
        Database.connect();
        
        int userID = 1;
        EntryRepository entryRepository = Database.getEntries();
        
        entryRepository.nukeEntries(1);
        entryRepository.nukeEntries(3);
        
        Entry e1 = entryRepository.addEntry(new Entry(-1, 1, 100, 1, null));
        Entry e2 = entryRepository.addEntry(new Entry(-1, 3, 101, 2, null));
        Entry e3 = entryRepository.addEntry(new Entry(-1, 1, 102, 10,  null));
        Entry e4 = entryRepository.addEntry(new Entry(-1, 1, 104, 2, null));

        entryRepository.checkEntry(e3.entryID());
        entryRepository.checkEntry(e4.entryID());


        try {exportEntriesAsCsv(1);}
        catch (IOException e) {e.printStackTrace();}


        try {
         exportEntriesAsPdf(userID);   
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        entryRepository.nukeEntries(1);
        entryRepository.nukeEntries(3);

        Database.disconnect();
    }
    
    
    public static void exportEntriesAsCsv(int userID) throws IOException {
        EntryRepository entryRepository = Database.getEntries();
        List <Entry> entries = entryRepository.getEntries(userID);
        File folder = new File("entry_exports/" + userID + "/csv");
            if (!folder.exists()) {
                folder.mkdirs();
            }
        File file = new File("entry_exports/" + userID + "/csv/entries_user_" + userID + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("entryID;userID;productID;quantity;checkDate\n");
            for (Entry entry : entries) {
                if (entry.checkDate() != null) {
                    writer.write(entry.entryID() + ";" + entry.userID() + ";" + entry.productID() + ";" + entry.quantity() + ";" + entry.checkDate().toString());
                } else {
                    writer.write(entry.entryID() + ";" + entry.userID() + ";" + entry.productID() + ";" + entry.quantity() + ";" + "-");    
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
     */


    public static void exportEntriesAsPdf(int userID) throws IOException {
        EntryRepository entryRepository = Database.getEntries();
        ProductRepository productRepository = Database.getProducts();
        UserRepository userRepository = Database.getUsers();
        List<Entry> entries = entryRepository.getEntries(userID);

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
                }
                else {
                    throw new IllegalArgumentException("User existiert nicht");
                }

                content.showText(userFirstname + " " +  userLastname + "'s Formula Emendi");
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
                
                for (Entry entry : entries) {
                    Product product = productRepository.getProduct(entry.productID()).orElse(null);
                    String produktName = product != null ? product.name() : "Unbekannt";
                    String menge = String.valueOf(entry.quantity());
                    int preis = product != null ? product.price() : 0;
                    String preisStr = String.format("%.2f EUR", preis / 100.0);
                    gesamtpreis += preis;
                    String datum = entry.checkDate() != null ? entry.checkDate().toString() : "-";

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
                    if (y < 100) break; // Mehr Platz für Gesamtpreis
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
            File file = new File("entry_exports/" + userID + "/pdf/entries_user_" + userID + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".pdf");
            doc.save(file);
        }
    }
}

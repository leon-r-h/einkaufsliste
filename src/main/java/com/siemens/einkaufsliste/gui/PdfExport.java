package com.siemens.einkaufsliste.gui;

import com.siemens.einkaufsliste.database.model.Entry;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.ProductRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.siemens.einkaufsliste.database.repository.EntryRepository;

import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;

public class PdfExport {


    public static void main(String[] args) {

        Database.connect();
        
        int userID = 3;
        EntryRepository entryRepository = Database.getEntries();
        
        entryRepository.nukeEntries(1);
        entryRepository.nukeEntries(3);
        
        Entry e1 = entryRepository.addEntry(new Entry(-1, 1, 100, 1, null));
        Entry e2 = entryRepository.addEntry(new Entry(-1, 3, 101, 2, null));
        Entry e3 = entryRepository.addEntry(new Entry(-1, 1, 102, 10,  null));
        Entry e4 = entryRepository.addEntry(new Entry(-1, 1, 104, 2, null));

        entryRepository.checkEntry(e3.entryID());
        entryRepository.checkEntry(e4.entryID());
        
        try {
         exportEntriesAsPdf(userID);   
        } catch (IOException e) {
            e.printStackTrace();
        }

        entryRepository.nukeEntries(1);
        entryRepository.nukeEntries(3);

        Database.disconnect();
    }

    public static void exportEntriesAsPdf(int userID) throws IOException {
        EntryRepository entryRepository = Database.getEntries();
        List<Entry> entries = entryRepository.getEntries(userID);

        int width = 600;
        int height = Math.max(100 + entries.size() * 30, 200);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(width, height));
            doc.addPage(page);

            // PdfBoxGraphics2D erzeugen
            PdfBoxGraphics2D g2 = new PdfBoxGraphics2D(doc, width, height);
                // Hintergrund weiß
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);

                // Überschrift
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.drawString("Einkaufsliste", 20, 40);

                // Tabellenkopf
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString("Produkt", 20, 80);
                g2.drawString("Menge", 250, 80);
                g2.drawString("Datum", 350, 80);

                // Einträge
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                int y = 110;
                ProductRepository productRepository = Database.getProducts();
                for (Entry entry : entries) {
                    Product product = productRepository.getProduct(entry.productID()).orElse(null);
                    String produktName = product != null ? product.name() : "Unbekannt";
                    String menge = String.valueOf(entry.quantity());
                    String datum = entry.checkDate() != null ? entry.checkDate().toString() : "-";

                    g2.drawString(produktName, 20, y);
                    g2.drawString(menge, 250, y);
                    g2.drawString(datum, 350, y);
                    y += 30;
                }

                // In PDF schreiben
                g2.dispose(); 
                try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                    content.drawForm(g2.getXFormObject());
                }
                
            

            File folder = new File("entry_exports/" + userID);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File("entry_exports/" + userID + "/entries_user_" + userID + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".pdf");
            doc.save(file);
        }
    }
}
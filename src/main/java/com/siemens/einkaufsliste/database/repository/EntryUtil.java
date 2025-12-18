package com.siemens.einkaufsliste.database.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.siemens.einkaufsliste.database.model.Entry;

public final class EntryUtil {

    private EntryUtil() {}

    /* Testing Main Method */
    
    public static void main(String[] args) {

        
        Database.connect();
        File file = new File ("../../../../../../../test/java/com/siemens/einkaufsliste/exports/testfile.csv");
        EntryRepository entryRepository = Database.getEntries();
        Entry e1 = entryRepository.addEntry(new Entry(-1, 1, 100, 1, null));
        Entry e2 = entryRepository.addEntry(new Entry(-1, 3, 101, 2, null));
        Entry e3 = entryRepository.addEntry(new Entry(-1, 1, 102, 10,  null));
        Entry e4 = entryRepository.addEntry(new Entry(-1, 1, 104, 2, null));

        entryRepository.checkEntry(e3.entryID());
        entryRepository.checkEntry(e4.entryID());

        
        try {saveEntriesToFile(1, file);}
        catch (IOException e) {e.printStackTrace();}
        Database.disconnect();
    }
    
    
    public static void saveEntriesToFile(int userID, File file) throws IOException {
        EntryRepository entryRepository = Database.getEntries();
        List <Entry> entries = entryRepository.getEntries(userID);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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

    public static List<Entry> loadEntriesfromFile(int userID, File file) throws IOException {
        EntryRepository entryRepository = Database.getEntries();
        List<Entry> entries = entryRepository.getEntries(userID);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(";");
                int entryID = Integer.parseInt(attributes[0]);
                int newUserID = Integer.parseInt(attributes[1]);
                int productID = Integer.parseInt(attributes[2]);
                int quantity = Integer.parseInt(attributes[3]);
                LocalDate checkDate = null;
                if (!attributes[4].equals("-"))
                    checkDate = LocalDate.parse(attributes[4]);
                entries.add(new Entry(entryID, newUserID, productID, quantity, checkDate));
            }
            return entries;
        }
    }
}

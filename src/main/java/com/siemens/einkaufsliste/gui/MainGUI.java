package com.siemens.einkaufsliste.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatLightLaf;
import com.siemens.einkaufsliste.database.model.Product;
import com.siemens.einkaufsliste.database.repository.Database;

public class MainGUI extends JFrame {

    JPanel itemSelecter;
    List<JPanel> itemPanels;
    JPanel userList;
    
    JPanel itemsContainer;
    JPanel userItemsContainer;

    JScrollPane items;
    JScrollPane userItems;

    JTextField searchField;

    public MainGUI(){
        itemPanels = new ArrayList();
        this.setTitle("Einkaufsliste");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);

        // Positioniert das Fenster in die Mitte
        Rectangle bounds = this.getGraphicsConfiguration().getBounds();
        int x = bounds.x + (bounds.width  - this.getWidth())  / 2;
        int y = bounds.y + (bounds.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        itemSelecter = new JPanel();
        userList = new JPanel();
        
        // Container für Items erstellen mit BoxLayout
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        
        userItemsContainer = new JPanel();
        userItemsContainer.setLayout(new BoxLayout(userItemsContainer, BoxLayout.Y_AXIS));

        // ScrollPanes um die Container
        items = new JScrollPane(itemsContainer);
        userItems = new JScrollPane(userItemsContainer);
        items.getVerticalScrollBar().setUnitIncrement(16);
        userItems.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, itemSelecter, userList);
        split.setDividerLocation(500);

        itemSelecter.setLayout(new BorderLayout());
        userList.setLayout(new BorderLayout());
        
        // Suchleiste mit Button
        JPanel searchBar = new JPanel(new BorderLayout());
        searchBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField = new JTextField();
        JButton searchButton = new JButton("🔍");

        // Logik der Suche
        searchButton.addActionListener(e -> {
            for(int i = 0; i < itemPanels.size(); i++){
                itemsContainer.remove(itemPanels.get(i));
            }
            itemsContainer.revalidate();
            itemsContainer.repaint();
            if (searchField.getText().equals("")){
                List<Product> productList = Database.getProducts().getProducts();
                for(int i = 0; i < productList.size(); i++){
                    addItem(productList.get(i).name());
                }
            }
        });

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchButton, BorderLayout.EAST);

        itemSelecter.add(searchBar, BorderLayout.PAGE_START);

        JPanel userTitel = new JPanel(new BorderLayout());
        userTitel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel userTitelLabel = new JLabel("Meine Einkaufsliste");
        JButton userRefresh = new JButton("Refresh");

        userTitel.add(userTitelLabel, BorderLayout.CENTER);
        userTitel.add(userRefresh, BorderLayout.EAST);

        userList.add(userTitel, BorderLayout.PAGE_START);
        
        itemSelecter.add(items, BorderLayout.CENTER);
        userList.add(userItems, BorderLayout.CENTER);
        
        this.add(split);
        
        this.setVisible(true); 

        List<Product> productList = Database.getProducts().getProducts();
        if (searchField.getText().equals("")){
            for(int i = 0; i < productList.size(); i++){
                addItem(productList.get(i).name());
            }
        }
    }

    private void addItem(String itemName) {
        // Item-Panel erstellen
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Item-Name
        JLabel nameLabel = new JLabel(itemName);
        itemPanel.add(nameLabel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        JButton editBtn = new JButton("+");
        
        // Button-Aktionen
        editBtn.addActionListener(e -> {
            String anzahlstr = JOptionPane.showInputDialog("Anzahl", 1);
            try{
                int anzahl = Integer.parseInt(anzahlstr);
                addItemToUser(nameLabel.getText(), anzahl);
            } catch (Exception error) {
                
            }
        });
        
        
        buttonPanel.add(editBtn);
        itemPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Zum itemsContainer hinzufügen (nicht zur ScrollPane!)
        itemsContainer.add(itemPanel);
        itemPanels.add(itemPanel);
        
        itemsContainer.revalidate();
        itemsContainer.repaint();
    }

    private void addItemToUser(String itemName, int Anzahl) {
        // Item-Panel erstellen
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Checkbox links
        JCheckBox checkBox = new JCheckBox();
        itemPanel.add(checkBox, BorderLayout.LINE_START);
        
        // Mittlerer Bereich: Name und Anzahl
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        centerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(itemName);
        centerPanel.add(nameLabel, BorderLayout.LINE_START);
        
        JLabel anzahlLabel = new JLabel("" + Anzahl);
        anzahlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centerPanel.add(anzahlLabel, BorderLayout.LINE_END);
        
        itemPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Delete-Button rechts
        JButton deleteBtn = new JButton("Löschen");
        deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "'" + nameLabel.getText() + "' löschen?", "Bestätigung", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                userItemsContainer.remove(itemPanel);
                userItemsContainer.revalidate();
                userItemsContainer.repaint();
            }
        });
        
        itemPanel.add(deleteBtn, BorderLayout.LINE_END);
        
        // Zum userItemsContainer hinzufügen
        userItemsContainer.add(itemPanel);
        
        userItemsContainer.revalidate();
        userItemsContainer.repaint();
    }

    public static void main(String[] args){
        
        Database.connect();
        FlatLightLaf.setup();
        new MainGUI();
    }
}
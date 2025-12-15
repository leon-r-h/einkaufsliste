package com.siemens.einkaufsliste.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatLightLaf;

public class MainGUI extends JFrame {
    JPanel itemSelecter;
    JPanel userList;
    
    // Container für die Items (nicht die ScrollPane!)
    JPanel itemsContainer;
    JPanel userItemsContainer;

    JScrollPane items;
    JScrollPane userItems;

    public MainGUI(){
        this.setTitle("Einkaufsliste");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600); 

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

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, itemSelecter, userList);
        split.setDividerLocation(500); // Optional: Teiler in der Mitte

        itemSelecter.setLayout(new BorderLayout());
        userList.setLayout(new BorderLayout());
        
        itemSelecter.add(new JTextField(), BorderLayout.PAGE_START);
        userList.add(new JTextField(), BorderLayout.PAGE_START);
        
        itemSelecter.add(items, BorderLayout.CENTER);
        userList.add(userItems, BorderLayout.CENTER);
        
        addItemWithName("Beispiel Item 1");
        addItemWithName("Beispiel Item 2");
        addItemWithName("Beispiel Item 3");
        
        this.add(split);
        
        this.setVisible(true); 
    }

    private void addItemWithName(String itemName) {
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
        
        JButton editBtn = new JButton("Bearbeiten");
        JButton deleteBtn = new JButton("Löschen");
        
        // Button-Aktionen
        editBtn.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Neuer Name:", nameLabel.getText());
            if (newName != null && !newName.trim().isEmpty()) {
                nameLabel.setText(newName.trim());
            }
        });
        
        deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "'" + nameLabel.getText() + "' löschen?", "Bestätigung", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                itemsContainer.remove(itemPanel);
                itemsContainer.revalidate();
                itemsContainer.repaint();
            }
        });
        
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        itemPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Zum itemsContainer hinzufügen (nicht zur ScrollPane!)
        itemsContainer.add(itemPanel);
        itemsContainer.add(Box.createVerticalStrut(5));
        
        itemsContainer.revalidate();
        itemsContainer.repaint();
    }

    public static void main(String[] args){
        FlatLightLaf.setup();
        new MainGUI();
    }
}
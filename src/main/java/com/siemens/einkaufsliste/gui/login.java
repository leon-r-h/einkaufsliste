package com.siemens.einkaufsliste.gui;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.Optional;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.Database;

public class Login extends JDialog {
    private User authenticatedUser = null;
    
    public Login(JFrame parent){
        super(parent, "Anmelden", true); // modal dialog
        anmelden();
    }

    public User anmelden(){
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(300, 190);

        // Zentrieren relativ zum Parent oder Bildschirm
        if (getOwner() != null) {
            setLocationRelativeTo(getOwner());
        } else {
            Rectangle bounds = this.getGraphicsConfiguration().getBounds();
            int x = bounds.x + (bounds.width  - this.getWidth())  / 2;
            int y = bounds.y + (bounds.height - this.getHeight()) / 2;
            this.setLocation(x, y);
        }

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        // Hauptpanel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Textfeld
        JLabel name = new JLabel("Email");
        panel.add(name);
        JTextField nameField = new JTextField();
        panel.add(nameField);

        // Passwortfeld
        JLabel passwort = new JLabel("Passwort");
        panel.add(passwort);
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton buttonCancel = new JButton("Abbrechen");

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                authenticatedUser = null;
                Login.this.dispose();
            }
        });
        panel.add(buttonCancel);

        JButton buttonDone = new JButton("Anmelden");

        buttonDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                
                // Hier würdest du normalerweise die Authentifizierung durchführen
                Optional<User> tmp = Database.getUsers().getUser(nameField.getText());
                if(tmp.isPresent()){
                    authenticatedUser = tmp.get();
                    if(authenticatedUser.password().equals(passwordField.getText())){

                    } else {
                        System.out.println("das ist das problem");
                        authenticatedUser = null;
                    }
                } else {
                    authenticatedUser = null;
                }
                
                Login.this.dispose();
            }
        });
        panel.add(buttonDone);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 3, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel2.add(new JLabel(""));
        JButton buttonRegister = new JButton("Register");

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Login.this.dispose();
                new Register(); // Übergib hier ggf. das Parent-Fenster
            }
        });
        panel2.add(buttonRegister);
        panel2.add(new JLabel(""));

        this.add(panel);
        this.add(panel2);
        this.setVisible(true);
        
        return authenticatedUser;
    }
    
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
    
    public static void main(String[] args){
        Database.connect();
        FlatLightLaf.setup();

        // Beispiel-Verwendung
        SwingUtilities.invokeLater(() -> {
            Login loginDialog = new Login(null);
            User user = loginDialog.getAuthenticatedUser();
            
            if (user != null) {
                System.out.println("Benutzer erfolgreich angemeldet!");
                // Hier kannst du das Hauptfenster öffnen
            } else {
                System.out.println("Anmeldung abgebrochen");
                System.exit(0);
            }
        });
    }
}
package com.siemens.einkaufsliste.gui;

import java.awt.BorderLayout; // Import für BorderLayout
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout; // Für den Footer-Panel
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;
import com.siemens.einkaufsliste.database.repository.Database;

public class Register extends JFrame {

    public Register() {
        this.setTitle("Registrierung"); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(550, 405);

        // Fenster zentrieren
        Rectangle bounds = this.getGraphicsConfiguration().getBounds();
        int x = bounds.x + (bounds.width - this.getWidth()) / 2;
        int y = bounds.y + (bounds.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        // Verwende BorderLayout für den Haupt-Frame, um Formular und Footer zu trennen
        this.setLayout(new BorderLayout());

        // Hauptformular-Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        // Gesamt-Padding um das Formular herum
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Innenabstand für alle Komponenten
        gbc.fill = GridBagConstraints.HORIZONTAL; // Komponenten füllen horizontalen Platz aus

        int row = 0; // Zähler für die aktuelle Zeile

        // Vorname
        gbc.gridx = 0; // Erste Spalte
        gbc.gridy = row; // Aktuelle Zeile
        gbc.anchor = GridBagConstraints.WEST; // Label linksbündig ausrichten
        gbc.weightx = 0; // Label nimmt keinen zusätzlichen horizontalen Platz ein
        formPanel.add(new JLabel("Vorname:"), gbc);

        gbc.gridx = 1; // Zweite Spalte
        gbc.weightx = 1.0; // Textfeld nimmt den gesamten verfügbaren horizontalen Platz ein
        JTextField nameField = new JTextField(20); // Vorgegebene Breite für das Textfeld
        formPanel.add(nameField, gbc);
        row++; // Nächste Zeile

        // Nachname
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nachname:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField lastNameField = new JTextField(20);
        formPanel.add(lastNameField, gbc);
        row++;

        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        row++;

        // Passwortfeld
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Passwort:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        row++;

        // Geburtsdatum
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Geburtsdatum:"), gbc);

        // Inneres Panel für Tag/Monat/Jahr (verwendet auch GridBagLayout für interne Anordnung)
        JPanel datumFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbcDate = new GridBagConstraints();
        gbcDate.insets = new Insets(0, 2, 0, 2); // Kleinerer interner Abstand
        gbcDate.fill = GridBagConstraints.HORIZONTAL;

        JTextField dayField = new JTextField(2); // Kleinere bevorzugte Breite
        JTextField monthField = new JTextField(2);
        JTextField yearField = new JTextField(4); // Größere bevorzugte Breite

        gbcDate.gridx = 0;
        gbcDate.weightx = 0.2; // Tag nimmt 20% Platz
        datumFields.add(dayField, gbcDate);

        gbcDate.gridx = 1;
        gbcDate.weightx = 0.2; // Monat nimmt 20% Platz
        datumFields.add(monthField, gbcDate);

        gbcDate.gridx = 2;
        gbcDate.weightx = 0.6; // Jahr nimmt 60% Platz
        datumFields.add(yearField, gbcDate);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Das gesamte Datum-Panel nimmt den vollen horizontalen Platz ein
        formPanel.add(datumFields, gbc);
        row++;

        // Checkbox (Newsletter)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Newsletter abonnieren:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JCheckBox newsletterBox = new JCheckBox();
        gbc.anchor = GridBagConstraints.WEST; // Checkbox linksbündig ausrichten
        gbc.fill = GridBagConstraints.NONE; // Checkbox soll sich nicht horizontal strecken
        formPanel.add(newsletterBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; // fill-Einstellung für nächste Komponenten zurücksetzen
        row++;

        // Radio Buttons (Geschlecht)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Geschlecht:"), gbc);

        JPanel radioPanel = new JPanel(); // Verwendet standardmäßig FlowLayout, was hier gut passt
        JRadioButton male = new JRadioButton("Männlich");
        JRadioButton female = new JRadioButton("Weiblich");
        JRadioButton other = new JRadioButton("Andere");
        other.setSelected(true);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);
        radioPanel.add(male);
        radioPanel.add(female);
        radioPanel.add(other);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST; // Radio-Panel linksbündig ausrichten
        gbc.fill = GridBagConstraints.HORIZONTAL; // Erlaubt dem Panel, sich bei Bedarf zu füllen
        formPanel.add(radioPanel, gbc);
        row++;

        // --- Aktions-Buttons (Abbrechen und Registrieren) ---
        // Ein Panel für diese Buttons, um sie zu gruppieren
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Zentriertes FlowLayout
        JButton buttonCancel = new JButton("Abbrechen");
        JButton buttonDone = new JButton("Registrieren");

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Register.this.dispose(); // Schließt das Registrierungsfenster
                // Optional: Hier könnte man das Login-Fenster wieder öffnen
                // new login().setVisible(true);
            }
        });

        buttonDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String firstName = nameField.getText();
                String lastName = lastNameField.getText();
                String password = new String(passwordField.getPassword());
                boolean newsletterSubscribed = newsletterBox.isSelected();
                String day = dayField.getText();
                String month = monthField.getText();
                String year = yearField.getText();
                String email = emailField.getText();
                Gender gender;
                if (male.isSelected()) {
                    gender = Gender.MALE;
                } else if (female.isSelected()) {
                    gender = Gender.FEMALE;
                } else {
                    gender = Gender.OTHER;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                try {
                    User newUser = new User(firstName, lastName, LocalDate.parse(day + "-" + month + "-" + year, formatter), gender, email, password, newsletterSubscribed);
                    
                    Database.getUsers().registerUser(newUser);

                    JOptionPane.showMessageDialog(Register.this, "Registrierung erfolgreich!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    Register.this.dispose(); 
                    new login().setVisible(true);
                } catch (IllegalArgumentException err) {
                    JOptionPane.showMessageDialog(Register.this, "Registrierung nicht erfolgreich!", "Erneut versuchen", JOptionPane.INFORMATION_MESSAGE);
                } catch (DateTimeParseException err) {
                    JOptionPane.showMessageDialog(Register.this, "Datum ist Falsch!", "Erneut versuchen", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
        });

        buttonPanel.add(buttonCancel);
        buttonPanel.add(buttonDone);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2; // Buttons spannen sich über beide Spalten
        gbc.anchor = GridBagConstraints.CENTER; // Button-Panel zentrieren
        gbc.fill = GridBagConstraints.NONE; // Button-Panel selbst nicht strecken
        gbc.weighty = 1.0; // Schiebt andere Komponenten nach oben, gibt Platz nach unten
        formPanel.add(buttonPanel, gbc);
        row++;

        this.add(formPanel, BorderLayout.CENTER); // Fügt das Hauptformular-Panel in die Mitte des Frames ein

        // --- Footer-Panel (für den Login-Button) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Zentriertes FlowLayout
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding

        JLabel loginPrompt = new JLabel("Bereits registriert?");
        JButton buttonLogin = new JButton("Login");

        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Register.this.dispose();
                new login().setVisible(true); // Öffnet das Login-Fenster
            }
        });
        footerPanel.add(loginPrompt);
        footerPanel.add(buttonLogin);

        this.add(footerPanel, BorderLayout.SOUTH); // Fügt den Footer unten an

        this.setVisible(true);
    }

    public static void main(String[] args) {
        // Sicherstellen, dass die Datenbankverbindung vor dem GUI-Start hergestellt wird
        // Oder handle dies im Login/Registrierungsprozess selbst
        Database.connect();

        // FlatLaf Look and Feel für ein modernes Aussehen
        FlatLightLaf.setup();
        new Register();
    }
}

// Dummy-Klasse für das Login-Fenster, damit der Code kompilierbar ist
// In einer echten Anwendung wäre dies eine vollständige Klasse
class login extends JFrame {
    public login() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm
        add(new JLabel("<html><div style='text-align: center;'>Login-Bildschirm (Platzhalter)<br>Hier kommen deine Login-Felder hin.</div></html>"), BorderLayout.CENTER);
        // Hier könnten die tatsächlichen Login-Komponenten hinzugefügt werden
    }
}
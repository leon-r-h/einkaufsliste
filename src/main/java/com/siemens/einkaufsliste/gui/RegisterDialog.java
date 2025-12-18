package com.siemens.einkaufsliste.gui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.FlatLightLaf;
import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.model.User.Gender;
import com.siemens.einkaufsliste.database.repository.Database;

public final class RegisterDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField dateField;
    private JComboBox<Gender> genderBox;
    private JCheckBox newsletterBox;

    private User registeredUser;

    private RegisterDialog(Frame parent) {
        super(parent, "Formula Emendi", true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.initializeInterface();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    private void initializeInterface() {
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Sign up");
        title.putClientProperty("FlatLaf.styleClass", "h1");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        content.add(new JLabel("Name:"), gbc);

        JPanel namePanel = new JPanel(new GridBagLayout());
        GridBagConstraints nameGbc = new GridBagConstraints();
        nameGbc.fill = GridBagConstraints.HORIZONTAL;
        nameGbc.weightx = 0.5;

        firstNameField = new JTextField(10);
        firstNameField.putClientProperty("JTextField.placeholderText", "First Name");
        namePanel.add(firstNameField, nameGbc);

        nameGbc.insets = new Insets(0, 5, 0, 0);
        lastNameField = new JTextField(10);
        lastNameField.putClientProperty("JTextField.placeholderText", "Last Name");
        namePanel.add(lastNameField, nameGbc);

        gbc.gridx = 1;
        content.add(namePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        content.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        emailField.putClientProperty("JTextField.placeholderText", "user@email.com");
        gbc.gridx = 1;
        content.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        content.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", "Minimum 8 characters");
        passwordField.putClientProperty("JTextField.showRevealButton", true);
        gbc.gridx = 1;
        content.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        content.add(new JLabel("Details:"), gbc);

        JPanel detailPanel = new JPanel(new GridBagLayout());
        GridBagConstraints detailGbc = new GridBagConstraints();
        detailGbc.fill = GridBagConstraints.HORIZONTAL;
        detailGbc.weightx = 0.6;

        dateField = new JTextField(10);
        dateField.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        detailPanel.add(dateField, detailGbc);

        detailGbc.weightx = 0.4;
        detailGbc.insets = new Insets(0, 5, 0, 0);
        genderBox = new JComboBox<>(Gender.values());
        detailPanel.add(genderBox, detailGbc);

        gbc.gridx = 1;
        content.add(detailPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        newsletterBox = new JCheckBox("Subscribe to Newsletter");
        content.add(newsletterBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton registerButton = new JButton("Register");
        registerButton.putClientProperty("FlatLaf.styleClass", "accent");
        registerButton.addActionListener(e -> attemptRegistration());

        buttonPanel.add(cancelButton);
        buttonPanel.add(new JPanel() {{ setPreferredSize(new java.awt.Dimension(5, 0)); }});
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 4, 4, 4);
        content.add(buttonPanel, gbc);

        this.getRootPane().setDefaultButton(registerButton);
        this.add(content);
    }

    private void attemptRegistration() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String dateStr = dateField.getText().trim();
        Gender gender = (Gender) genderBox.getSelectedItem();
        boolean news = newsletterBox.isSelected();

        if (first.isBlank() || last.isBlank() || email.isBlank() || pass.isBlank() || dateStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            User user = new User(0, first, last, birthDate, gender, email, pass, news);
            Database.getUsers().registerUser(user);

            Optional<User> dbUser = Database.getUsers().getUser(email);
            if (dbUser.isPresent()) {
                this.registeredUser = dbUser.get();
                JOptionPane.showMessageDialog(this, "Account created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Optional<User> showRegisterDialog(Frame owner) {
        RegisterDialog dialog = new RegisterDialog(owner);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.registeredUser);
    }

    public static void main(String[] args) {
    	FlatLightLaf.setup();

    	Database.connect();

    	System.out.println(RegisterDialog.showRegisterDialog(null));
    }
}
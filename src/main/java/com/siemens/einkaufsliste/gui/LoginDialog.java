package com.siemens.einkaufsliste.gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.Database;

public final class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField emailField;
	private JPasswordField passwordField;
	private User authenticatedUser;
	private final Frame owner;

	private LoginDialog(Frame parent) {
		super(parent, "Formula Emendi", true);
		this.owner = parent;
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

		JLabel title = new JLabel("Sign in");
		title.putClientProperty("FlatLaf.styleClass", "h1");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		content.add(title, gbc);

		gbc.gridwidth = 1;
		gbc.gridy++;
		content.add(new JLabel("Email:", SwingConstants.RIGHT), gbc);

		emailField = new JTextField(20);
		emailField.putClientProperty("JTextField.placeholderText", "user@siemens.com");
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		content.add(emailField, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		content.add(new JLabel("Password:", SwingConstants.RIGHT), gbc);

		passwordField = new JPasswordField(20);
		passwordField.putClientProperty("JTextField.placeholderText", "Password");
		passwordField.putClientProperty("JTextField.showRevealButton", true);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		content.add(passwordField, gbc);

		JButton signInButton = new JButton("Sign in");
		signInButton.addActionListener(e -> attemptLogin());
		signInButton.putClientProperty("FlatLaf.styleClass", "accent");

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.weightx = 0.0;
		content.add(signInButton, gbc);
		this.getRootPane().setDefaultButton(signInButton);

		gbc.gridy++;
		content.add(new JSeparator(), gbc);

		JPanel signUpPanel = new JPanel();
		signUpPanel.add(new JLabel("Not registered yet?"));

		JButton signUpButton = new JButton("Sign Up");
		signUpButton.addActionListener(e -> openRegister());
		signUpPanel.add(signUpButton);

		gbc.gridy++;
		content.add(signUpPanel, gbc);

		this.add(content);
	}

	private void attemptLogin() {
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());

		if (email.isBlank() || password.isBlank()) {
			JOptionPane.showMessageDialog(this, "Fields cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Optional<User> userOpt = Database.getUsers().getUser(email);

		if (userOpt.isPresent() && userOpt.get().password().equals(password)) {
			authenticatedUser = userOpt.get();
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
			passwordField.setText("");
		}
	}

	private void openRegister() {
		this.dispose();
		Optional<User> newUser = RegisterDialog.showRegisterDialog(owner);
		newUser.ifPresent(user -> this.authenticatedUser = user);
	}

	public static Optional<User> showLoginDialog(Frame owner) {
		LoginDialog dialog = new LoginDialog(owner);
		dialog.setVisible(true);
		return Optional.ofNullable(dialog.authenticatedUser);
	}
}
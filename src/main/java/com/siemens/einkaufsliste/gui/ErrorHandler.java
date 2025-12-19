package com.siemens.einkaufsliste.gui;

import java.awt.Component;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.siemens.einkaufsliste.database.repository.DataAccessException;

public final class ErrorHandler {

	private ErrorHandler() {

	}

	public static void handle(Component parentComponent, Throwable throwable, Logger logger) {
		if (throwable instanceof ExecutionException) {
			throwable = throwable.getCause();
		}

		if (throwable instanceof DataAccessException) {
			logger.log(Level.WARNING, "Database operation failed", throwable);

			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentComponent,
					"Could not communicate with the database.", "Formula Emendi", JOptionPane.ERROR_MESSAGE));
		} else if (throwable instanceof InterruptedException) {
			logger.log(Level.INFO, "Operation interrupted");
			Thread.currentThread().interrupt();
		} else {
			logger.log(Level.SEVERE, "Unexpected application error", throwable);
		}
	}

}

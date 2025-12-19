package com.siemens.einkaufsliste.gui;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

public final class TaskQueue {

	private static final Logger LOGGER = Logger.getLogger(TaskQueue.class.getName());

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "TaskQueueWorker");
		thread.setDaemon(true);
		return thread;
	});

	private TaskQueue() {

	}

	public static <T> void submit(Callable<T> task, Consumer<T> onSuccess) {
		EXECUTOR.submit(() -> {
			try {
				T result = task.call();
				if (onSuccess != null) {
					SwingUtilities.invokeLater(() -> onSuccess.accept(result));
				}
			} catch (Exception e) {
				ErrorHandler.handle(null, e, LOGGER);
			}
		});
	}

	public static void submit(Runnable task, Runnable onSuccess) {
		submit(() -> {
			task.run();
			return null;
		}, ignored -> {
			if (onSuccess != null) {
				onSuccess.run();
			}
		});
	}

	public static void shutdown() {
		EXECUTOR.shutdownNow();
	}

}

package com.siemens.einkaufsliste;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Main program that recursively reads every file in a directory as text and
 * prints:
 *
 * relative/path/to/File.ext: " ... file contents ... "
 *
 * Usage: java Main -> scans current working directory java Main
 * /path/to/project/root -> scans provided folder
 */
public class SrcTestExporter {

	public static void main(String[] args) {
		Path root = (args != null && args.length > 0) ? Paths.get(args[0]) : Paths.get("./src/main/java/com/siemens/einkaufsliste/gui");
		if (!Files.exists(root)) {
			System.err.println("Path does not exist: " + root.toAbsolutePath());
			System.exit(2);
		}

		try (Stream<Path> stream = Files.walk(root)) {
			stream.filter(Files::isRegularFile).forEach(path -> {
				Path rel = root.toAbsolutePath().normalize().relativize(path.toAbsolutePath().normalize());
				// Use forward slashes in output like your example
				String relativePath = rel.toString().replace(FileSystems.getDefault().getSeparator(), "/");

				System.out.println(relativePath + ":");
				System.out.println("\"");
				String content = readFileAsText(path);
				System.out.println(content);
				System.out.println("\"");
				System.out.println(); // blank line between files
			});
		} catch (IOException e) {
			System.err.println("Error walking directory " + root + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Attempts to read a file as UTF-8 text. If UTF-8 decoding fails, falls back to
	 * ISO-8859-1. On any other I/O error returns a clear placeholder line.
	 */
	private static String readFileAsText(Path path) {
		try {
			byte[] bytes = Files.readAllBytes(path);
			// Try UTF-8 first
			try {
				return new String(bytes, StandardCharsets.UTF_8);
			} catch (Exception ignore) {
				// fallback
				return new String(bytes, Charset.forName("ISO-8859-1"));
			}
		} catch (IOException ioe) {
			return "<unable to read file: " + ioe.getMessage() + ">";
		}
	}
}

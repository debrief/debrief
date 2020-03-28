package org.mwc.debrief.pepys.model.db.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import junit.framework.TestCase;

public class ConfigurationReader {

	public static class ConfigurationReaderTest extends TestCase {

		public void testConfigurationRead() {
			final String testConfiguration = "[database]\n" + "db_username = abcdef\n" + "db_password = abcdef\n"
					+ "db_host = localhost\n" + "db_port = 5432\n" + "db_name = pepys2\n" + "[archive]\n"
					+ "user = abcdef\n" + "password = abcdef";
			final InputStream stream = new ByteArrayInputStream(testConfiguration.getBytes());
			final DatabaseConfiguration databaseConfigurationTest = new DatabaseConfiguration();
			parseConfigurationFile(databaseConfigurationTest, stream);

			// Check Database Categories
			final HashMap<String, String> databaseCategory = databaseConfigurationTest.getCategory("database");
			final HashMap<String, String> archiveCategory = databaseConfigurationTest.getCategory("archive");

			assertTrue("databaseCategory - size", databaseCategory.size() == 5);
			assertTrue("archiveCategory - size", archiveCategory.size() == 2);

			assertTrue("DBUsername", databaseCategory.get("db_username").equals("abcdef"));
			assertTrue("DBPassword", databaseCategory.get("db_password").equals("abcdef"));
			assertTrue("DBHost", databaseCategory.get("db_host").equals("localhost"));
			assertTrue("DBPort", databaseCategory.get("db_port").equals("5432"));
			assertTrue("archiveCategory - User", archiveCategory.get("user").equals("abcdef"));

		}

		public void testProcess() {
			assertTrue("Process Test", process("ABZabz").equals("NOMnom"));
		}
	}

	static final String DEMILITER = "_";

	static final String CATEGORY_START = "[";

	static final String CATEGORY_END = "]";

	private static final String CONFIG_SEPARATOR = "=";

	private static final String CONFIG_COMMENT_CHAR = "#";

	public static String getCategoryName(final String category) {
		// We are assuming it is a category
		if (category != null && category.length() >= 2) {
			return category.substring(1, category.length() - 1);
		}
		return null;
	}

	public static boolean isCategory(final String line) {
		if (line != null) {
			final String trimmedLine = line.trim();
			return trimmedLine.startsWith(CATEGORY_START) && trimmedLine.endsWith(CATEGORY_END);
		}
		return false;
	}

	public static boolean isToProcess(final String str) {
		return str != null && str.startsWith(DEMILITER) && str.endsWith(DEMILITER);
	}

	public static void parseConfigurationFile(final DatabaseConfiguration configuration,
			final InputStream inputStream) {
		final Scanner scanner = new Scanner(inputStream);
		try {
			String currentCategoryName = null;
			while (scanner.hasNextLine()) {
				final String currentLine = scanner.nextLine().trim();

				if (isCategory(currentLine)) {
					currentCategoryName = getCategoryName(currentLine);
				} else if (currentLine.startsWith(CONFIG_COMMENT_CHAR)) {
					// Just a comment
				} else if (currentCategoryName != null) {
					parseRegularLine(configuration.getCategory(currentCategoryName), currentLine);
				}
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	public static void parseRegularLine(final HashMap<String, String> category, final String str) {
		if (str != null && str.contains(CONFIG_SEPARATOR)) {
			final String tokens[] = str.split(CONFIG_SEPARATOR);
			if (tokens.length == 2) {
				if (isToProcess(tokens[1].trim())) {
					tokens[1] = process(tokens[1].trim());
				}
				category.put(tokens[0].trim(), tokens[1].trim());
			}
		}
	}

	public static String process(final String _str) {
		final String str = _str.substring(1, _str.length() - 1);
		final char[] strArray = str.toCharArray();
		final int delta = 'Z' - 'A' + 1;
		for (int i = 0; i < strArray.length; i++) {
			if (Character.isLetter(str.charAt(i))) {
				strArray[i] = (char) (strArray[i] - 13);
				if ((Character.isUpperCase(str.charAt(i)) && strArray[i] < 'A')
						|| (!Character.isUpperCase(str.charAt(i)) && strArray[i] < 'a')) {
					strArray[i] += delta;
				}
			}
		}
		return new String(strArray);
	}

}

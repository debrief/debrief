/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.Testing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;

/**
 * Compares output against a committed golden file, so that a library upgrade
 * which changes what Debrief writes shows up as a test failure.
 *
 * <p>
 * Golden files live in {@code org.mwc.debrief.legacy/test_data/golden/}. Tests
 * run with their own plugin as the working directory, so the default location
 * is {@code ../org.mwc.debrief.legacy/test_data/golden}.
 *
 * <p>
 * To regenerate the golden files, run the tests with the system property
 * {@value #REGENERATE_PROPERTY}{@code =true}, or the environment variable
 * {@value #REGENERATE_ENV}{@code =true} (the environment variable reaches the
 * forked test JVMs of both Tycho Surefire and Ant's junit task). Each test then
 * writes its output as the new golden file and passes. Review the diff and
 * explain it in the pull request. See
 * {@code org.mwc.debrief.legacy/test_data/golden/README.md}.
 */
public final class GoldenFile {

	/** system property: when "true", write the golden file instead of comparing */
	public static final String REGENERATE_PROPERTY = "debrief.golden.regenerate";

	/** environment variable equivalent of {@link #REGENERATE_PROPERTY} */
	public static final String REGENERATE_ENV = "DEBRIEF_GOLDEN_REGENERATE";

	/** system property: overrides the directory holding the golden files */
	public static final String DIR_PROPERTY = "debrief.golden.dir";

	private static final String DEFAULT_DIR = "../org.mwc.debrief.legacy/test_data/golden";

	/** placeholder written in place of a masked value */
	public static final String MASK = "MASKED";

	/**
	 * check the output against the named golden file, or write it if
	 * regenerating. Line endings are normalised to \n before comparing.
	 *
	 * @param name   file name within the golden directory, e.g.
	 *               "lite/sample.dpf"
	 * @param actual the output to check
	 */
	public static void assertMatches(final String name, final String actual) throws IOException {
		final String normalised = normalise(actual);
		final File golden = new File(dir(), name);
		if (regenerating()) {
			golden.getParentFile().mkdirs();
			Files.write(golden.toPath(), normalised.getBytes(StandardCharsets.UTF_8));
			System.out.println("Regenerated golden file " + golden.getCanonicalPath());
			return;
		}
		if (!golden.isFile()) {
			throw new AssertionFailedError("Golden file " + golden.getCanonicalPath()
					+ " is missing. Run the test with -D" + REGENERATE_PROPERTY + "=true (or " + REGENERATE_ENV
					+ "=true) to create it.");
		}
		final String expected = normalise(new String(Files.readAllBytes(golden.toPath()), StandardCharsets.UTF_8));
		if (!expected.equals(normalised)) {
			final File actualFile = File.createTempFile("golden-actual-", "-" + golden.getName());
			Files.write(actualFile.toPath(), normalised.getBytes(StandardCharsets.UTF_8));
			throw new AssertionFailedError("Output differs from golden file " + golden.getCanonicalPath() + " "
					+ firstDifference(expected, normalised) + ". Actual output written to "
					+ actualFile.getCanonicalPath() + ". If the change is intended, regenerate with -D"
					+ REGENERATE_PROPERTY + "=true and explain why in the pull request.");
		}
	}

	/** the directory holding the golden files */
	public static File dir() {
		return new File(System.getProperty(DIR_PROPERTY, DEFAULT_DIR));
	}

	/**
	 * replace the value of each named XML attribute with {@link #MASK}, for
	 * fields that change from run to run (creation time, user name and so on)
	 */
	public static String maskAttributes(final String xml, final String... attributeNames) {
		String res = xml;
		for (final String attr : attributeNames) {
			res = Pattern.compile("(\\b" + Pattern.quote(attr) + "\\s*=\\s*\")[^\"]*(\")").matcher(res)
					.replaceAll("$1" + MASK + "$2");
		}
		return res;
	}

	private static String firstDifference(final String expected, final String actual) {
		final String[] e = expected.split("\n", -1);
		final String[] a = actual.split("\n", -1);
		final int n = Math.min(e.length, a.length);
		for (int i = 0; i < n; i++) {
			if (!e[i].equals(a[i])) {
				return "at line " + (i + 1) + ": expected [" + e[i] + "] but was [" + a[i] + "]";
			}
		}
		return "in length: expected " + e.length + " lines but was " + a.length;
	}

	private static String normalise(final String s) {
		return s.replace("\r\n", "\n").replace('\r', '\n');
	}

	/** whether this run writes the golden files rather than checking them */
	public static boolean isRegenerating() {
		return regenerating();
	}

	private static boolean regenerating() {
		return Boolean.getBoolean(REGENERATE_PROPERTY) || "true".equalsIgnoreCase(System.getenv(REGENERATE_ENV));
	}

	private GoldenFile() {
	}
}

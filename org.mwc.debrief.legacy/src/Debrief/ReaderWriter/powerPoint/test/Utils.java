/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Utils {
	public static final String testFolder = "../org.mwc.debrief.legacy/test_data";

	/**
	 * Quoted algorithm from here
	 * https://stackoverflow.com/questions/30738470/what-is-the-best-way-to-compare-tar-archives-in-junit-testing
	 *
	 * @param zip1
	 * @param files1
	 * @param zip2
	 * @param files2
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private static final void assertMembersEqual(final ZipFile zip1, final HashMap<String, ZipEntry> files1,
			final ZipFile zip2, final HashMap<String, ZipEntry> files2) throws IOException {
		if (files1.size() != files2.size()) {
			fail("Different Sizes, expected " + Integer.toString(files1.size()) + " found "
					+ Integer.toString(files2.size()));
		}

		for (final String key : files1.keySet()) {
			if (!files2.containsKey(key)) {
				fail("Expected file not in target " + key);
			}
			final String file1 = IOUtils.toString(zip1.getInputStream(files1.get(key)));
			final String file2 = IOUtils.toString(zip2.getInputStream(files2.get(key)));
			assertEquals(file1, file2);
		}
	}

	/**
	 * @param archive1
	 * @param archive2
	 * @throws ZipException
	 * @throws IOException
	 */
	public static final void assertZipEquals(final String archive1, final String archive2)
			throws ZipException, IOException {
		// Get Archives
		final ZipFile zipFile1 = new ZipFile(new File(archive1));
		final ZipFile zipFile2 = new ZipFile(new File(archive2));

		// Get Member Hash
		final HashMap<String, ZipEntry> files1 = getMembers(zipFile1);
		final HashMap<String, ZipEntry> files2 = getMembers(zipFile2);

		// Compare Files
		assertMembersEqual(zipFile1, files1, zipFile2, files2);
	}

	public static boolean compareDirectoriesStructures(final File generatedPptxTemporaryFolder,
			final File expectedPptxTemporaryFolder) {
		final HashSet<String> gen = new HashSet<>();
		for (final File genFile : FileUtils.listFiles(generatedPptxTemporaryFolder, null, true)) {
			gen.add(genFile.getAbsolutePath().substring(generatedPptxTemporaryFolder.getAbsolutePath().length()));
		}
		final HashSet<String> exp = new HashSet<>();
		for (final File expFile : FileUtils.listFiles(expectedPptxTemporaryFolder, null, true)) {
			exp.add(expFile.getAbsolutePath().substring(expectedPptxTemporaryFolder.getAbsolutePath().length()));
		}

		return exp.containsAll(gen) && gen.containsAll(exp);
	}

	/**
	 * @param archive
	 * @return
	 * @throws IOException
	 */
	private static final HashMap<String, ZipEntry> getMembers(final ZipFile archive) throws IOException {
		final HashMap<String, ZipEntry> map = new HashMap<String, ZipEntry>();
		@SuppressWarnings("unchecked")
		final Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) archive.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			map.put(entry.getName(), entry);
		}
		return map;
	}
}

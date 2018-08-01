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
	public static final String testFolder = "expected_test_results";

	public static boolean compareDirectoriesStructures(File generatedPptxTemporaryFolder,
			File expectedPptxTemporaryFolder) {
		HashSet<String> gen = new HashSet<>();
		for (File genFile : FileUtils.listFiles(generatedPptxTemporaryFolder, null, true)) {
			gen.add(genFile.getAbsolutePath().substring(generatedPptxTemporaryFolder.getAbsolutePath().length()));
		}
		HashSet<String> exp = new HashSet<>();
		for (File expFile : FileUtils.listFiles(expectedPptxTemporaryFolder, null, true)) {
			exp.add(expFile.getAbsolutePath().substring(expectedPptxTemporaryFolder.getAbsolutePath().length()));
		}

		return exp.containsAll(gen) && gen.containsAll(exp);
	}

	/**
	 * @param archive1
	 * @param archive2
	 * @throws ZipException
	 * @throws IOException
	 */
	public static final void assertZipEquals(String archive1, String archive2) throws ZipException, IOException {
		// Get Archives
		ZipFile zipFile1 = new ZipFile(new File(archive1));
		ZipFile zipFile2 = new ZipFile(new File(archive2));

		// Get Member Hash
		HashMap<String, ZipEntry> files1 = getMembers(zipFile1);
		HashMap<String, ZipEntry> files2 = getMembers(zipFile2);

		// Compare Files
		assertMembersEqual(zipFile1, files1, zipFile2, files2);
	}

	/**
	 * @param archive
	 * @return
	 * @throws IOException
	 */
	private static final HashMap<String, ZipEntry> getMembers(ZipFile archive) throws IOException {
		HashMap<String, ZipEntry> map = new HashMap<String, ZipEntry>();
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) archive.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			map.put(entry.getName(), entry);
		}
		return map;
	}

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
	private static final void assertMembersEqual(ZipFile zip1, HashMap<String, ZipEntry> files1, ZipFile zip2,
			HashMap<String, ZipEntry> files2) throws IOException {
		if (files1.size() != files2.size()) {
			fail("Different Sizes, expected " + Integer.toString(files1.size()) + " found "
					+ Integer.toString(files2.size()));
		}

		for (String key : files1.keySet()) {
			if (!files2.containsKey(key)) {
				fail("Expected file not in target " + key);
			}
			String file1 = IOUtils.toString(zip1.getInputStream(files1.get(key)));
			String file2 = IOUtils.toString(zip2.getInputStream(files2.get(key)));
			assertEquals(file1, file2);
		}
	}
}

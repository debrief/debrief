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

package MWC.Utilities.ReaderWriter;

import static MWC.Utilities.ReaderWriter.SafeArchive.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.SafeArchive.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SafeArchiveTest {
	private static File makeZip(final File dir, final String name, final String[] entries, final int bytesPerEntry)
			throws IOException {
		final File res = new File(dir, name);
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(res))) {
			final byte[] buf = new byte[64 * 1024];
			for (final String entry : entries) {
				zos.putNextEntry(new ZipEntry(entry));
				int remaining = bytesPerEntry;
				while (remaining > 0) {
					final int n = Math.min(buf.length, remaining);
					zos.write(buf, 0, n);
					remaining -= n;
				}
				zos.closeEntry();
			}
		}
		return res;
	}

	private File _dir;

	@BeforeEach
	public void setUp() throws Exception {
		_dir = Files.createTempDirectory("safeArchive").toFile();
	}

	@AfterEach
	public void tearDown() throws Exception {
		deleteTree(_dir);
	}

	private static void deleteTree(final File f) {
		final File[] children = f.listFiles();
		if (children != null) {
			for (final File c : children) {
				deleteTree(c);
			}
		}
		f.delete();
	}

	@Test
	public void testExtractsNormalArchive() throws IOException {
		final File zip = makeZip(_dir, "ok.zip", new String[] { "a.txt", "sub/b.txt", "sub/deeper/c.txt" }, 100);
		final File dest = new File(_dir, "out");
		extractZip(zip, dest, new Limits(10, 1000, 1000));
		assertEquals(100, new File(dest, "sub/deeper/c.txt").length());
		assertEquals(100, new File(dest, "a.txt").length());
	}

	@Test
	public void testRejectsZipSlip() throws IOException {
		final File dest = new File(_dir, "out");
		final String[] bad = { "../evil.txt", "sub/../../evil.txt", "/tmp/evil.txt", "C:/evil.txt", "..\\evil.txt" };
		for (final String name : bad) {
			final File zip = makeZip(_dir, "slip.zip", new String[] { "ok.txt", name }, 10);
			try {
				extractZip(zip, dest, new Limits(10, 1000, 1000));
				fail("should have rejected " + name);
			} catch (final ArchiveException e) {
				assertTrue(e.getMessage().contains("unsafe"), e.getMessage());
			}
			assertFalse(new File(_dir, "evil.txt").exists(), "nothing written outside dest");
		}
	}

	@Test
	public void testRejectsBombs() throws IOException {
		final File dest = new File(_dir, "out");
		// single entry larger than permitted
		final File big = makeZip(_dir, "big.zip", new String[] { "a.txt" }, 2 * 1024 * 1024);
		assertTrue(big.length() < 50 * 1024, "highly compressed");
		try {
			extractZip(big, dest, new Limits(10, 1024 * 1024, 100 * 1024 * 1024));
			fail("entry too big");
		} catch (final ArchiveException e) {
			assertTrue(e.getMessage().contains("larger than"), e.getMessage());
		}
		// total too large
		final File many = makeZip(_dir, "many.zip", new String[] { "a", "b", "c" }, 512 * 1024);
		try {
			extractZip(many, dest, new Limits(10, 1024 * 1024, 1024 * 1024));
			fail("total too big");
		} catch (final ArchiveException e) {
			assertTrue(e.getMessage().contains("larger than"), e.getMessage());
		}
		// too many entries
		try {
			extractZip(many, dest, new Limits(2, 1024 * 1024, 100 * 1024 * 1024));
			fail("too many entries");
		} catch (final ArchiveException e) {
			assertTrue(e.getMessage().contains("entries"), e.getMessage());
		}
	}

	@Test
	public void testLimitedStream() throws IOException {
		final InputStream ok = limit(new ByteArrayInputStream(new byte[100]), 100, "data");
		assertEquals(100, ok.readAllBytes().length);
		final InputStream tooBig = limit(new ByteArrayInputStream(new byte[101]), 100, "data");
		try {
			tooBig.readAllBytes();
			fail("should have thrown");
		} catch (final ArchiveException e) {
			assertTrue(e.getMessage().contains("larger than"));
		}
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy(new ByteArrayInputStream(new byte[50]), bos, 60, "data");
		assertEquals(50, bos.size());
	}
}

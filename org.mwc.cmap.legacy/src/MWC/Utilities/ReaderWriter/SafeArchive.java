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

import junit.framework.TestCase;

/**
 * Helpers for reading archives (zip, gzip) that may come from an untrusted
 * source. Guards against:
 * <ul>
 * <li>zip-slip: entry names that would be written outside the destination
 * directory (<code>../</code>, absolute paths, drive letters)</li>
 * <li>zip/gzip bombs: small archives that inflate to huge sizes or contain huge
 * numbers of entries</li>
 * </ul>
 */
public final class SafeArchive {

	/**
	 * thrown when an archive breaks one of the limits, or contains an unsafe entry
	 * name
	 */
	public static class ArchiveException extends IOException {
		private static final long serialVersionUID = 1L;

		public ArchiveException(final String message) {
			super(message);
		}
	}

	/**
	 * the limits applied when reading an archive
	 */
	public static final class Limits {
		public final int maxEntries;
		public final long maxEntryBytes;
		public final long maxTotalBytes;

		public Limits(final int maxEntries, final long maxEntryBytes, final long maxTotalBytes) {
			this.maxEntries = maxEntries;
			this.maxEntryBytes = maxEntryBytes;
			this.maxTotalBytes = maxTotalBytes;
		}
	}

	/**
	 * input stream that throws an {@link ArchiveException} once more than the
	 * permitted number of bytes has been read. Closing it does not close the
	 * wrapped stream (so it can wrap a single zip entry).
	 */
	private static final class LimitedInputStream extends FilterInputStream {
		private final long _max;
		private final String _what;
		private long _count;

		LimitedInputStream(final InputStream in, final long max, final String what) {
			super(in);
			_max = max;
			_what = what;
		}

		private int check(final int n) throws ArchiveException {
			if (n > 0) {
				_count += n;
				if (_count > _max) {
					throw new ArchiveException(_what + " is larger than the permitted " + _max + " bytes when uncompressed");
				}
			}
			return n;
		}

		@Override
		public void close() {
			// don't close the underlying stream, the owner does that
		}

		@Override
		public synchronized void mark(final int readlimit) {
			// not supported
		}

		@Override
		public boolean markSupported() {
			return false;
		}

		@Override
		public int read() throws IOException {
			final int res = in.read();
			if (res != -1) {
				check(1);
			}
			return res;
		}

		@Override
		public int read(final byte[] b, final int off, final int len) throws IOException {
			return check(in.read(b, off, len));
		}

		@Override
		public synchronized void reset() throws IOException {
			throw new IOException("mark/reset not supported");
		}

		@Override
		public long skip(final long n) throws IOException {
			return check((int) Math.min(Integer.MAX_VALUE, in.skip(n)));
		}
	}

	public static class SafeArchiveTest extends TestCase {
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

		@Override
		protected void setUp() throws Exception {
			_dir = Files.createTempDirectory("safeArchive").toFile();
		}

		@Override
		protected void tearDown() throws Exception {
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

		public void testExtractsNormalArchive() throws IOException {
			final File zip = makeZip(_dir, "ok.zip", new String[] { "a.txt", "sub/b.txt", "sub/deeper/c.txt" }, 100);
			final File dest = new File(_dir, "out");
			extractZip(zip, dest, new Limits(10, 1000, 1000));
			assertEquals(100, new File(dest, "sub/deeper/c.txt").length());
			assertEquals(100, new File(dest, "a.txt").length());
		}

		public void testRejectsZipSlip() throws IOException {
			final File dest = new File(_dir, "out");
			final String[] bad = { "../evil.txt", "sub/../../evil.txt", "/tmp/evil.txt", "C:/evil.txt", "..\\evil.txt" };
			for (final String name : bad) {
				final File zip = makeZip(_dir, "slip.zip", new String[] { "ok.txt", name }, 10);
				try {
					extractZip(zip, dest, new Limits(10, 1000, 1000));
					fail("should have rejected " + name);
				} catch (final ArchiveException e) {
					assertTrue(e.getMessage(), e.getMessage().contains("unsafe"));
				}
				assertFalse("nothing written outside dest", new File(_dir, "evil.txt").exists());
			}
		}

		public void testRejectsBombs() throws IOException {
			final File dest = new File(_dir, "out");
			// single entry larger than permitted
			final File big = makeZip(_dir, "big.zip", new String[] { "a.txt" }, 2 * 1024 * 1024);
			assertTrue("highly compressed", big.length() < 50 * 1024);
			try {
				extractZip(big, dest, new Limits(10, 1024 * 1024, 100 * 1024 * 1024));
				fail("entry too big");
			} catch (final ArchiveException e) {
				assertTrue(e.getMessage(), e.getMessage().contains("larger than"));
			}
			// total too large
			final File many = makeZip(_dir, "many.zip", new String[] { "a", "b", "c" }, 512 * 1024);
			try {
				extractZip(many, dest, new Limits(10, 1024 * 1024, 1024 * 1024));
				fail("total too big");
			} catch (final ArchiveException e) {
				assertTrue(e.getMessage(), e.getMessage().contains("larger than"));
			}
			// too many entries
			try {
				extractZip(many, dest, new Limits(2, 1024 * 1024, 100 * 1024 * 1024));
				fail("too many entries");
			} catch (final ArchiveException e) {
				assertTrue(e.getMessage(), e.getMessage().contains("entries"));
			}
		}

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

	/**
	 * copy the stream, failing once more than max bytes have been copied
	 *
	 * @return the number of bytes copied
	 */
	public static long copy(final InputStream in, final OutputStream out, final long max, final String what)
			throws IOException {
		final byte[] buf = new byte[8192];
		long total = 0;
		int n;
		while ((n = in.read(buf)) != -1) {
			total += n;
			if (total > max) {
				throw new ArchiveException(what + " is larger than the permitted " + max + " bytes when uncompressed");
			}
			out.write(buf, 0, n);
		}
		return total;
	}

	/**
	 * extract the zip file into the destination directory, checking each entry
	 * name stays inside the directory, and applying the size limits. The actual
	 * inflated byte counts are used, not the (untrusted) sizes declared in the
	 * archive.
	 *
	 * @param zipFile the archive
	 * @param destDir where to put it (created if necessary)
	 * @param limits  the limits to apply
	 * @throws ArchiveException if the archive breaks a limit or contains an unsafe
	 *                          name. Files extracted before the problem was found
	 *                          are left in place.
	 */
	public static void extractZip(final File zipFile, final File destDir, final Limits limits) throws IOException {
		final File destCanonical = destDir.getCanonicalFile();
		if (!destCanonical.isDirectory() && !destCanonical.mkdirs()) {
			throw new IOException("Unable to create directory:" + destCanonical);
		}

		try (ZipFile zip = new ZipFile(zipFile)) {
			if (zip.size() > limits.maxEntries) {
				throw new ArchiveException(zipFile.getName() + " has more than the permitted " + limits.maxEntries
						+ " entries (" + zip.size() + ")");
			}
			long total = 0;
			int count = 0;
			final Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				if (++count > limits.maxEntries) {
					throw new ArchiveException(
							zipFile.getName() + " has more than the permitted " + limits.maxEntries + " entries");
				}
				final File target = safeTarget(destCanonical, entry.getName());
				if (entry.isDirectory()) {
					if (!target.isDirectory() && !target.mkdirs()) {
						throw new IOException("Unable to create directory:" + target);
					}
					continue;
				}
				final File parent = target.getParentFile();
				if (!parent.isDirectory() && !parent.mkdirs()) {
					throw new IOException("Unable to create directory:" + parent);
				}
				final long remaining = limits.maxTotalBytes - total;
				final long entryMax = Math.min(limits.maxEntryBytes, remaining);
				try (InputStream in = zip.getInputStream(entry); OutputStream out = new FileOutputStream(target)) {
					final String what = entryMax == remaining ? zipFile.getName() : "Entry " + entry.getName();
					total += copy(in, out, entryMax, what);
				}
			}
		}
	}

	/**
	 * wrap the stream so that it fails once more than max bytes have been read.
	 * Closing the returned stream does not close the wrapped one.
	 *
	 * @param in   stream to wrap (e.g. a GZIPInputStream or ZipInputStream entry)
	 * @param max  number of bytes permitted
	 * @param what description used in the error message
	 */
	public static InputStream limit(final InputStream in, final long max, final String what) {
		return new LimitedInputStream(in, max, what);
	}

	/**
	 * check this entry name is safe to extract, and return the file to write it
	 * to
	 *
	 * @param destCanonical the canonical destination directory
	 * @param name          entry name from the archive
	 * @return where to write the entry
	 * @throws ArchiveException if the name is absolute, contains "..", or resolves
	 *                          outside the destination
	 */
	public static File safeTarget(final File destCanonical, final String name) throws IOException {
		final String normalised = name.replace('\\', '/');
		final boolean absolute = normalised.startsWith("/") || normalised.matches("^[A-Za-z]:.*");
		boolean parentRef = false;
		for (final String part : normalised.split("/")) {
			if ("..".equals(part)) {
				parentRef = true;
			}
		}
		if (normalised.isEmpty() || absolute || parentRef) {
			throw new ArchiveException("Archive contains an unsafe entry name:" + name);
		}
		final File target = new File(destCanonical, normalised).getCanonicalFile();
		if (!target.toPath().startsWith(destCanonical.toPath())) {
			throw new ArchiveException("Archive contains an unsafe entry name:" + name);
		}
		return target;
	}

	private SafeArchive() {
		// static utility
	}
}

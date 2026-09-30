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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.UnpackFunction;
import net.lingala.zip4j.exception.ZipException;

public class UnpackFunctionTest {
	private final String folderToUnpack = Utils.testFolder + File.separator + "UnpackPresentation" + File.separator
			+ "designed.pptx";
	private final String expectedFolder = Utils.testFolder + File.separator + "PackPresentation" + File.separator
			+ "designedFolder";

	/**
	 * create a pptx (zip) containing the named entries, each of the given size
	 */
	private static File makePptx(final File dir, final String[] entries, final int bytesPerEntry) throws IOException {
		final File res = new File(dir, "template.pptx");
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

	@Test
	public void testZipSlipTemplateRejected() throws IOException {
		final File dir = Files.createTempDirectory("zipSlip").toFile();
		try {
			final File unpackTo = new File(dir, "a" + File.separator + "b");
			final File pptx = makePptx(dir, new String[] { "ppt/presentation.xml", "../../evil.txt" }, 10);
			try {
				new UnpackFunction().unpackFunction(pptx.getAbsolutePath(), unpackTo.getAbsolutePath());
				fail("zip-slip entry should have been rejected");
			} catch (final ZipException | DebriefException e) {
				// expected
			}
			assertFalse("nothing written outside the unpack folder", new File(dir, "evil.txt").exists());
		} finally {
			FileUtils.deleteDirectory(dir);
		}
	}

	@Test
	public void testZipBombTemplateRejected() throws IOException {
		final File dir = Files.createTempDirectory("zipBomb").toFile();
		try {
			// a small file that inflates past the template limit
			final int size = (int) (UnpackFunction.MAX_TEMPLATE_BYTES / 4) + 1;
			final File pptx = makePptx(dir, new String[] { "a.xml", "b.xml", "c.xml", "d.xml" }, size);
			assertTrue("highly compressed", pptx.length() < UnpackFunction.MAX_TEMPLATE_BYTES / 100);
			try {
				new UnpackFunction().unpackFunction(pptx.getAbsolutePath(), new File(dir, "out").getAbsolutePath());
				fail("oversized template should have been rejected");
			} catch (final ZipException | DebriefException e) {
				// expected
			}
		} finally {
			FileUtils.deleteDirectory(dir);
		}
	}

	@Test
	public void testUnpackFunctionString() throws ZipException, DebriefException, IOException {
		final String generatedFolder = new UnpackFunction().unpackFunction(folderToUnpack);
		assertTrue(Utils.compareDirectoriesStructures(new File(generatedFolder), new File(expectedFolder)));
		FileUtils.deleteDirectory(new File(generatedFolder));
	}

}

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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PackPresentation;
import net.lingala.zip4j.exception.ZipException;

public class PackPresentationTest {
	private final String folderToPack = Utils.testFolder + File.separator + "PackPresentation" + File.separator
			+ "designedFolder";
	private final String folderToPackTest = Utils.testFolder + File.separator + "PackPresentation" + File.separator
			+ "designedFolderTest";
	private final String expectedPptx = Utils.testFolder + File.separator + "PackPresentation" + File.separator
			+ "designed.pptx";

	@Test
	public void testPack() throws IOException, ZipException, DebriefException {
		FileUtils.copyDirectory(new File(folderToPack), new File(folderToPackTest), true);
		final String generatedPptx = new PackPresentation().pack(null, folderToPackTest);

		assertFalse(new File(folderToPackTest).exists());
		Utils.assertZipEquals(generatedPptx, expectedPptx);
		new File(generatedPptx).delete();
	}

}

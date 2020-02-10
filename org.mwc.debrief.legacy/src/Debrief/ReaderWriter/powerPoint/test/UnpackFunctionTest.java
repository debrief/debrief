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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

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

	@Test
	public void testUnpackFunctionString() throws ZipException, DebriefException, IOException {
		final String generatedFolder = new UnpackFunction().unpackFunction(folderToUnpack);
		assertTrue(Utils.compareDirectoriesStructures(new File(generatedFolder), new File(expectedFolder)));
		FileUtils.deleteDirectory(new File(generatedFolder));
	}

}

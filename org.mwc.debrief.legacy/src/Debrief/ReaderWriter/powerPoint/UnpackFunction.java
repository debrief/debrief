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
package Debrief.ReaderWriter.powerPoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
import MWC.Utilities.ReaderWriter.SafeArchive;
import net.lingala.zip4j.exception.ZipException;

public class UnpackFunction {

	/**
	 * largest permitted uncompressed size of a PPTX master template, in total
	 * and for any single entry. The largest template in the sample/test data
	 * inflates to 2.1 MB (largest entry 2.0 MB), but corporate templates can hold
	 * large background images, so allow plenty of headroom.
	 */
	public static final long MAX_TEMPLATE_BYTES = 64L * 1024 * 1024;

	/**
	 * most entries permitted in a PPTX master template. Sample templates have up
	 * to 63.
	 */
	public static final int MAX_TEMPLATE_ENTRIES = 256;

	public String unpackFunction(final String pptx_path) throws ZipException, DebriefException {
		return unpackFunction(pptx_path, "");
	}

	public String unpackFunction(final String pptx_path, final String unpack_path_in)
			throws ZipException, DebriefException {
		final String unpack_path;
		if (unpack_path_in.isEmpty()) {
			unpack_path = pptx_path.substring(0, pptx_path.length() - 5);
		} else {
			unpack_path = unpack_path_in;
		}

		// check if unpack_path is directory or not
		if (!Files.exists(Paths.get(pptx_path)) || pptx_path.isEmpty()) {
			throw new DebriefException("The PPTX master template hasn't been assigned");
		}

		if (!pptx_path.endsWith("pptx")) {
			throw new DebriefException("The PPTX master provided is not a pptx file");
		}

		// Unpack the pptx file
		Application.logError2(ToolParent.INFO, "Unpacking pptx file...", null);
		if (Files.notExists(Paths.get(unpack_path))) {
			new File(unpack_path).mkdir();
		}

		if (Files.exists(Paths.get(unpack_path))) {
			try {
				FileUtils.deleteDirectory(new File(unpack_path));
			} catch (final IOException e) {
				throw new DebriefException("Impossible to remove the directory " + unpack_path);
			}
		}

		// the template may come from anywhere, so check the entry names stay in
		// the unpack folder (zip-slip) and cap the size (zip bomb)
		try {
			SafeArchive.extractZip(new File(pptx_path), new File(unpack_path),
					new SafeArchive.Limits(MAX_TEMPLATE_ENTRIES, MAX_TEMPLATE_BYTES, MAX_TEMPLATE_BYTES));
		} catch (final IOException e) {
			throw new ZipException("Unable to unpack the PPTX master template " + pptx_path + ": " + e.getMessage(),
					e);
		}
		Application.logError2(ToolParent.INFO, "File unpacked at " + unpack_path, null);
		return unpack_path;
	}
}

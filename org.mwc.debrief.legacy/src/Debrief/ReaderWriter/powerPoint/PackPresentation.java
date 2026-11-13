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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;

public class PackPresentation {

	/** OOXML readers look for this entry first, so it leads the package */
	private static final String CONTENT_TYPES = "[Content_Types].xml";

	/**
	 * zip the contents of a folder (not the folder itself), with
	 * {@link #CONTENT_TYPES} first and the other files and sub-folders in name
	 * order. Sub-folders get their own entries, as zip4j wrote them.
	 *
	 * @param folder  the folder to zip
	 * @param zipFile the zip file to create
	 */
	static void zipFolder(final Path folder, final Path zipFile) throws IOException {
		final List<Path> paths = new ArrayList<>();
		try (Stream<Path> walk = Files.walk(folder)) {
			walk.filter(p -> !p.equals(folder)).sorted().forEach(paths::add);
		}
		final Path contentTypes = folder.resolve(CONTENT_TYPES);
		if (paths.remove(contentTypes)) {
			paths.add(0, contentTypes);
		}
		try (OutputStream out = Files.newOutputStream(zipFile);
				ZipOutputStream zip = new ZipOutputStream(out)) {
			zip.setLevel(Deflater.BEST_SPEED);
			for (final Path path : paths) {
				final String name = folder.relativize(path).toString().replace(File.separatorChar, '/');
				if (Files.isDirectory(path)) {
					zip.putNextEntry(new ZipEntry(name + "/"));
				} else {
					zip.putNextEntry(new ZipEntry(name));
					Files.copy(path, zip);
				}
				zip.closeEntry();
			}
		}
	}

	/**
	 * Given an unpacked pptx, it creates a pptx file.
	 *
	 * @param pptx_path   pptx file path (Optional). Null to create the unpack_path
	 *                    filename
	 * @param unpack_path Folder that contains the pptx slides/documents
	 * @throws DebriefException In case we don't provide the unpack path or it is
	 *                          not a directory
	 */
	public String pack(final String pptx_path, final String unpack_path)
			throws IOException, ZipException, DebriefException {
		if (unpack_path == null) {
			throw new DebriefException("Provide unpack_path (path to directory containing unpacked pptx)");
		}

		final String path;

		if (pptx_path != null) {
			path = pptx_path;
		} else if (unpack_path.charAt(unpack_path.length() - 1) == '/'
				|| unpack_path.charAt(unpack_path.length() - 1) == '\\') {
			path = unpack_path.substring(0, unpack_path.length() - 1) + ".pptx";
		} else {
			path = unpack_path + ".pptx";
		}

		// check if unpack_path is directory or not
		if (!Files.isDirectory(Paths.get(unpack_path))) {
			throw new DebriefException("unpack_path provided is not a directory");
		}

		// Pack the unpack_path folder to pptx_path pptx file
		Files.deleteIfExists(new File(path).toPath());
		zipFolder(Paths.get(unpack_path), Paths.get(path));
		Application.logError2(ToolParent.INFO, "File packed at " + path, null);

		try {
			FileUtils.deleteDirectory(new File(unpack_path));
		} catch (final IOException e) {
			throw new DebriefException("Impossible to remove the directory " + unpack_path);
		}
		return path;
	}
}

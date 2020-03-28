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

package org.mwc.debrief.pepys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;

public class NativeLibrariesLoader {

	private static void cleanDirectory(final File directory) {
		for (final File child : directory.listFiles()) {
			if (child.isDirectory()) {
				cleanDirectory(child);
			}
			child.delete();
		}
	}

	public static void loadBundledXuggler(final File nativeLibrariesDirectory, final Bundle bundle) throws IOException {
		nativeLibrariesDirectory.mkdirs();
		cleanDirectory(nativeLibrariesDirectory);
		String nativePath = null;
		if (OSUtils.WIN) {
			nativePath = OSUtils.IS_64BIT ? "/native/win64/" : "/native/win32/";
		}
		if (OSUtils.MAC && OSUtils.IS_64BIT) {
			nativePath = "/native/mac64/";
		}
		if (OSUtils.LINUX && OSUtils.IS_64BIT) {
			nativePath = "/native/linux/";
		}
		if (nativePath == null) {
			// we don't have bundled libraries for this os
			return;
		}
		final InputStream loadOrderStream = bundle.getResource(nativePath + "load-order").openStream();
		try {
			final List<String> loadOrder = IOUtils.readLines(loadOrderStream);
			for (String libraryToLoad : loadOrder) {
				libraryToLoad = libraryToLoad.trim();
				if (libraryToLoad.isEmpty()) {
					continue;
				}
				if (libraryToLoad.contains(Activator.MOD_SPATIALITE_NAME)) {
					Activator.modSpatialiteName = libraryToLoad;
				}
				final File libraryFile = new File(nativeLibrariesDirectory, libraryToLoad);
				FileUtils.copyURLToFile(bundle.getResource(nativePath + libraryToLoad), libraryFile);
				System.load(libraryFile.getCanonicalPath());
			}
		} finally {
			IOUtils.closeQuietly(loadOrderStream);
		}
	}

}

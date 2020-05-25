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

package org.mwc.debrief.model.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.mwc.debrief.pepys.Activator;

public class NativeLibrariesLoader {

	public static interface ModSpatialiteAssigner {
		public void assign(final String path);
	}

	private static void cleanDirectory(final File directory) {
		for (final File child : directory.listFiles()) {
			if (child.isDirectory()) {
				cleanDirectory(child);
			}
			child.delete();
		}
	}
	
	public static Set<String> previouslyLoaded = new HashSet<>();

	public static void loadBundledSpatialite(final File nativeLibrariesDirectory, final ModSpatialiteAssigner assigner)
			throws IOException {
		if (!previouslyLoaded.contains(nativeLibrariesDirectory.getAbsolutePath())) {
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
			final InputStream loadOrderStream = OSUtils.getInputStreamResource(NativeLibrariesLoader.class,
					nativePath + "load-order", Activator.PLUGIN_ID);
			final Scanner scanner = new Scanner(loadOrderStream);
			try {
				while (scanner.hasNextLine()) {
					final String libraryToLoad = scanner.nextLine().trim();
					if (libraryToLoad.isEmpty()) {
						continue;
					}
					if (libraryToLoad.contains(Activator.MOD_SPATIALITE_NAME)) {
						assigner.assign(libraryToLoad);
					}
					final File libraryFile = new File(nativeLibrariesDirectory, libraryToLoad);
					FileUtils.copyURLToFile(OSUtils.getURLResource(NativeLibrariesLoader.class, nativePath + libraryToLoad,
							Activator.PLUGIN_ID), libraryFile);
					System.load(libraryFile.getCanonicalPath());
				}
			} finally {
				if (scanner != null) {
					scanner.close();
				}
				if (loadOrderStream != null) {
					loadOrderStream.close();
				}
			}
			previouslyLoaded.add(nativeLibrariesDirectory.getAbsolutePath());
		}
	}

}

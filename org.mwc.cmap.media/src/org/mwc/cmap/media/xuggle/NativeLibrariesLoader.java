/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.media.xuggle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mwc.cmap.media.utility.OSUtils;
import org.osgi.framework.Bundle;

import com.xuggle.ferry.JNIHelper;

public class NativeLibrariesLoader {
	
	private static void cleanDirectory(File directory) {
		for (File child : directory.listFiles()) {
			if (child.isDirectory()) {
				cleanDirectory(child);
			}
			child.delete();
		}
	}
	
	public static void loadBundledXuggler(File nativeLibrariesDirectory, Bundle bundle) throws IOException {
		nativeLibrariesDirectory.mkdirs();
		cleanDirectory(nativeLibrariesDirectory);
		String nativePath = null;
		char versionSeparator = '-';
		if (OSUtils.WIN) {
			nativePath = OSUtils.IS_64BIT ? "/native/win64/" : "/native/win32/";
		}
		if (OSUtils.MAC && OSUtils.IS_64BIT) {
			nativePath = "/native/mac64/";
			versionSeparator = '.';
		}
		if (nativePath == null) {
			// we don't have bundled libraries for this os
			return;
		}
		InputStream loadOrderStream = bundle.getResource(nativePath + "load-order").openStream();
		try {
			List<String> loadOrder = IOUtils.readLines(loadOrderStream);
			for (String libraryToLoad : loadOrder) {
				libraryToLoad = libraryToLoad.trim();
				if (libraryToLoad.isEmpty()) {
					continue;
				}
				File libraryFile = new File(nativeLibrariesDirectory, libraryToLoad);
				FileUtils.copyURLToFile(
						bundle.getResource(nativePath + libraryToLoad),
						libraryFile
				);
				String libraryName = libraryToLoad.substring(0, libraryToLoad.lastIndexOf('.'));
				if (libraryName.startsWith("lib")) {
					libraryName = libraryName.substring(3);					
				}
				int indexOfSeparator = libraryName.lastIndexOf(versionSeparator);
				long libraryVersion = 0;
				try {
					libraryVersion = Long.parseLong(libraryName.substring(indexOfSeparator + 1));
				} catch (NumberFormatException ex) {
					// ignore
				}
				libraryName = libraryName.substring(0, indexOfSeparator);
				System.load(libraryFile.getCanonicalPath());
				JNIHelper.setManuallyLoadedLibrary(libraryName, libraryVersion);
			}
		} finally {
			IOUtils.closeQuietly(loadOrderStream);
		}
	}

}

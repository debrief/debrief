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

package org.mwc.debrief.lite.util;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

import org.mwc.debrief.lite.DebriefLiteApp;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ManifestUtils {

	public static Manifest getJarManifest(final Class<?> clazz) {
		final String className = clazz.getSimpleName() + ".class";
		final String classPath = clazz.getResource(className).toString();
		// IJ.log("classPath = " + classPath);
		if (!classPath.startsWith("jar")) { // Class not from JAR
			return null;
		}
		final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest = null;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		} catch (final IOException ignore) {
		}
		return manifest;
	}

	public static String readManifestVersion() {
		String retVal = null;

		final Manifest manifest = getJarManifest(DebriefLiteApp.class);
		// check that this is your manifest and do what you need or get the next one
		if (manifest != null && manifest.getMainAttributes() != null
				&& "Debrief Lite".equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
			retVal = manifest.getMainAttributes().getValue("Implementation-Version");
		}
		return retVal;
	}
}

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.pepys.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import Debrief.GUI.Frames.Application;

public class OSUtils {

	public static final boolean WIN;
	public static final boolean MAC;
	public static final boolean LINUX;
	public static final boolean IS_64BIT;
	static {
		final String os = System.getProperty("os.name").toLowerCase();
		boolean win = false, mac = false, linux = false;
		if (os.indexOf("win") != -1) {
			win = true;
		}
		if (os.indexOf("mac os") != -1) {
			mac = true;
		}
		if (os.indexOf("linux") != -1) {
			linux = true;
		}
		WIN = win;
		MAC = mac;
		LINUX = linux;
		final String jvmArch = System.getProperty("os.arch");
		IS_64BIT = jvmArch != null && jvmArch.contains("64");
	}

	public static InputStream getInputStreamResource(final Class clazz, final String resourcePath,
			final String pluginID) throws IOException {
		return getURLResource(clazz, resourcePath, pluginID).openStream();
	}

	public static URL getURLResource(final Class clazz, final String resourcePath, final String pluginID)
			throws MalformedURLException {
		final URL answer;
		final Bundle bundle = FrameworkUtil.getBundle(clazz);
		final String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

		if (bundle != null) {
			// We are running from bundle or from .jar
			answer = bundle.getResource(resourcePath);
		} else if (path.endsWith("jar")) {
			answer = clazz.getResource(resourcePath);
		} else {
			// We are running from Eclipse
			answer = new File(path.substring(0, path.indexOf(pluginID) + pluginID.length() + 1) + resourcePath).toURI()
					.toURL();
		}
		return answer;
	}

	public static String readFile(final Class clazz, final String url) throws IOException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(OSUtils.getInputStreamResource(clazz, url, Activator.PLUGIN_ID));
			final StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine());
				builder.append("\n");
			}

			return builder.toString();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}

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

package org.mwc.debrief.lite;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.swing.JOptionPane;

/**
 * Debrief Lite's entry point. It checks the Java version, then starts
 * {@link DebriefLiteApp}.
 * <p>
 * The rest of Lite is compiled for Java 21, so an older Java cannot even load
 * it, and fails with a bare {@code UnsupportedClassVersionError}. This class
 * alone is compiled for Java 8 (see build.xml), so it loads on any Java from 8
 * up and can tell the user what to do instead. It refers to
 * {@link DebriefLiteApp} only by name, so nothing compiled for Java 21 is loaded
 * until the check has passed.
 */
public final class LiteLauncher {

	/** the oldest Java that Debrief Lite runs on */
	static final int REQUIRED_JAVA = 21;

	static final String DOWNLOAD_URL = "https://adoptium.net/temurin/releases/?version=21";

	private static final String APP_CLASS = "org.mwc.debrief.lite.DebriefLiteApp";

	/**
	 * the major Java version, from a {@code java.specification.version} value:
	 * "1.8" for Java 8, then "9", "17", "21" and so on
	 *
	 * @return the version, or -1 if it cannot be read
	 */
	static int featureVersion(final String specVersion) {
		if (specVersion == null) {
			return -1;
		}
		final String version = specVersion.startsWith("1.") ? specVersion.substring(2) : specVersion;
		final int dot = version.indexOf('.');
		try {
			return Integer.parseInt(dot < 0 ? version : version.substring(0, dot));
		} catch (final NumberFormatException e) {
			return -1;
		}
	}

	static String message(final int found, final String javaHome) {
		return "Debrief Lite needs Java " + REQUIRED_JAVA + " or later.\n\n" + "It was started with Java "
				+ (found < 0 ? "(unknown version)" : String.valueOf(found)) + ", from:\n" + javaHome + "\n\n"
				+ "Install Java " + REQUIRED_JAVA + " (for example Eclipse Temurin " + REQUIRED_JAVA
				+ ") from:\n" + DOWNLOAD_URL + "\n\n" + "then run Debrief Lite with it.";
	}

	public static void main(final String[] args) throws Throwable {
		final int found = featureVersion(System.getProperty("java.specification.version"));
		if (found >= REQUIRED_JAVA) {
			try {
				Class.forName(APP_CLASS).getMethod("main", String[].class).invoke(null, (Object) args);
			} catch (final InvocationTargetException e) {
				throw e.getCause();
			}
			return;
		}

		final String text = message(found, System.getProperty("java.home"));
		System.err.println(text);
		if (!GraphicsEnvironment.isHeadless()) {
			showDialog(text);
		}
		System.exit(1);
	}

	private static void showDialog(final String text) {
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			splash.close();
		}
		final String[] options = { "Open download page", "Close" };
		final int choice = JOptionPane.showOptionDialog(null, text, "Debrief Lite: Java " + REQUIRED_JAVA + " needed",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (choice == 0 && Desktop.isDesktopSupported()
				&& Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(DOWNLOAD_URL));
			} catch (final Exception e) {
				// the URL is in the message, and on stderr
			}
		}
	}

	private LiteLauncher() {
	}
}

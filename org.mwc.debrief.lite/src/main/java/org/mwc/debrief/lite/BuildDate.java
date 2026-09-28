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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * The date Debrief Lite was built. The Ant build (build.xml, target
 * buildDate) writes it to the builddate.txt resource beside this class, so the
 * build doesn't have to rewrite a tracked source file. Builds that skip that
 * step (e.g. running from the IDE) report {@link #UNKNOWN}.
 *
 * @author Ayesha
 *
 */
public class BuildDate {
	static final String UNKNOWN = "development build";

	public static final String BUILD_DATE = readBuildDate();

	private static String readBuildDate() {
		try (InputStream in = BuildDate.class.getResourceAsStream("builddate.txt")) {
			if (in != null) {
				final String line = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).readLine();
				if (line != null && !line.trim().isEmpty()) {
					return line.trim();
				}
			}
		} catch (final IOException e) {
			// fall through to the default
		}
		return UNKNOWN;
	}
}

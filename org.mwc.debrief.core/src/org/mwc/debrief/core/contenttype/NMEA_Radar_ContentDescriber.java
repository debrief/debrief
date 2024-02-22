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
package org.mwc.debrief.core.contenttype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.content.IContentDescription;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.FlatFile.NMEA_Radar_FileImporter;
import MWC.GUI.ErrorLogger;

/**
 * import format test for SATC export files
 *
 * @author ian
 *
 */
@SuppressWarnings("restriction")
public class NMEA_Radar_ContentDescriber extends TextContentDescriber {

	@Override
	public int describe(final InputStream contents, final IContentDescription description) throws IOException {
		BufferedReader r = null;
		final AtomicBoolean res = new AtomicBoolean();
		try {
			r = new BufferedReader(new InputStreamReader(contents));
			final ErrorLogger logger = CorePlugin.getToolParent();
			final boolean canRead = NMEA_Radar_FileImporter.canLoad(logger, r);
			res.set(canRead);
		} catch (final Exception e) {
			CorePlugin.logError(IStatus.ERROR, "NMEA Radar content type error", e);
		} finally {
			try {
				if (r != null)
					r.close();
			} catch (final IOException e) {
				CorePlugin.logError(IStatus.ERROR, "Couldn't close file", e);
			}
		}
		if (res.get()) {
			return VALID;
		} else {
			return super.describe(contents, description);
		}
	}

}

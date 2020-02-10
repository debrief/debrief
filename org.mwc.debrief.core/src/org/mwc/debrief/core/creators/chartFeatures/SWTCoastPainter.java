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

package org.mwc.debrief.core.creators.chartFeatures;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.CoastlineSourcePrefsPage;
import org.mwc.debrief.core.DebriefPlugin;

import MWC.GUI.ToolParent;
import MWC.GUI.Chart.Painters.CoastPainter;

public class SWTCoastPainter extends CoastPainter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return
	 */
	@Override
	protected InputStream getCoastLineInput() {
		final ToolParent parent = CorePlugin.getToolParent();
		final String location = parent.getProperty(CoastlineSourcePrefsPage.PreferenceConstants.COASTLINE_FILE);

		System.out.println("loading coastline from:" + location);

		InputStream res = null;

		try {
			res = new FileInputStream(location);
		} catch (final FileNotFoundException e) {
			DebriefPlugin.logError(IStatus.ERROR, "Whilst loading coastline", e);
		}

		return res;
	}
}

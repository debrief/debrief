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
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.preferences.PrefsPage;

import Debrief.ReaderWriter.FlatFile.NMEA_Radar_FileImporter;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldLocation;

/** Loader for Radar data provided in NMEA 0183 format
 */
public class NMEA_Radar_Loader extends CoreLoader {

	public NMEA_Radar_Loader() {
		super("NMEA Radar File", ".txt");
	}

	@Override
	public boolean canLoad(final String fileName) {
		boolean res = false;
		if (super.canLoad(fileName)) {
			res = NMEA_Radar_FileImporter.canLoad(fileName, CorePlugin.getToolParent());
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers layers,
			final InputStream inputStream, final String fileName) {
		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				final NMEA_Radar_FileImporter importer = new NMEA_Radar_FileImporter();
				
				// get the origin
				final String radarLat = CorePlugin.getToolParent()
						.getProperty(PrefsPage.PreferenceConstants.NMEA_RADAR_LAT);
				final String radarLong = CorePlugin.getToolParent()
						.getProperty(PrefsPage.PreferenceConstants.NMEA_RADAR_LONG);
				
				if ((radarLat == null) || (radarLong == null)) {
					DebriefPlugin.logError(DebriefPlugin.ERROR, "Radar origin values not set", null);
				} else {
					try {
						final double latVal = Double.parseDouble(radarLat);
						final double longVal = Double.parseDouble(radarLong);
						final WorldLocation origin = new WorldLocation(latVal, longVal, 0d);
						
						// ok - get loading going
						final Action importAction = importer.importThis(origin, inputStream, layers,
								CorePlugin.getToolParent());

						final WrapDebriefAction dAction = new WrapDebriefAction(importAction);
						CorePlugin.run(dAction);
					} catch (final Exception e) {
						DebriefPlugin.logError(IStatus.ERROR, "Problem loading AIS datafile:" + fileName, e);
					}					
				}
				
			}
		};
	}
}

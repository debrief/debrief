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

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.geotools.gt2plot.GeoToolsLayer;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import it.geosolutions.imageio.stream.input.spi.URLImageInputStreamSpi;

/**
 * @author ian.mayo
 */
public class TifLoader extends CoreLoader {

	public TifLoader() {
		super(".tif", ".tif");

		GeoToolsLayer.registerTifUrlServiceProvider();
	}

	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers theLayers,
			final InputStream inputStream, final String fileName) {
		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				// create a layer name from the filename
				final File tmpFile = new File(fileName);
				final String layerName = tmpFile.getName();

				// ok - get loading going
				final ExternallyManagedDataLayer dl = new ExternallyManagedDataLayer(ChartBoundsWrapper.WORLDIMAGE_TYPE,
						layerName, fileName);
				theLayers.addThisLayer(dl);
			}
		};
	}
}

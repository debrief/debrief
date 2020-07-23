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

package org.mwc.cmap.geotools.gt2plot;

import java.io.File;
import java.util.Iterator;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.ToolParent;
import it.geosolutions.imageio.stream.input.spi.URLImageInputStreamSpi;

public abstract class GeoToolsLayer extends ExternallyManagedDataLayer {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String NATURAL_EARTH = "Natural Earth";

	public static void registerTifUrlServiceProvider() {
		boolean isRegistered = false;
		// Ensure that the provider is present
		try {
			final Iterator<ImageInputStreamSpi> iter = IIORegistry.getDefaultInstance()
					.getServiceProviders(ImageInputStreamSpi.class, true);

			while (iter.hasNext() && !isRegistered) {
				final ImageInputStreamSpi stream = iter.next();
				if (URLImageInputStreamSpi.class.equals(stream.getClass())) {
					isRegistered = true;
				}
			}

			if (!isRegistered) {
				IIORegistry.getDefaultInstance().registerServiceProvider(new URLImageInputStreamSpi(),
						ImageInputStreamSpi.class);
			}
		} catch (final IllegalArgumentException e) {
			Application.logError2(ToolParent.WARNING, "Failure in service registration", e);
		}
	}

	/**
	 * the map where we display ourselves
	 *
	 */
	protected transient MapContent _myMap;

	/**
	 * the GeoTools representation of this object
	 *
	 */
	protected transient Layer _myLayer;

	public GeoToolsLayer(final String dataType, final String layerName, final String fileName) {
		super(dataType, layerName, fileName);
	}

	/**
	 * forget about any existing map/layer
	 *
	 */
	public void clearMap() {
		if (_myLayer != null) {
			// remove ourselves from the map
			if (_myMap != null) {
				_myMap.removeLayer(_myLayer);
			}
			_myLayer.dispose();
			_myLayer = null;
		}
		_myMap = null;
	}

	@Override
	protected void finalize() throws Throwable {
		// ok, ditch the layer
		if (_myLayer != null)
			_myLayer.dispose();

		// and let the parent do it's stuff
		super.finalize();
	}

	public Layer getLayer() {
		return _myLayer;
	}

	/**
	 * load data from the specific file, return it as a GeoTools layer
	 *
	 * @param openFile the file to open
	 * @return the layer to load into a GeoTools object
	 */
	abstract protected Layer loadLayer(File openFile);

	/**
	 * tell this layer where to plot itself
	 *
	 * @param map
	 */
	public void setMap(final MapContent map) {
		clearMap();

		// remember the map
		_myMap = map;

		// read ourselves in
		final File openFile = new File(super.getFilename());
		if (openFile != null && openFile.exists()) {
			_myLayer = loadLayer(openFile);
		} else {
			Application.logError2(2, "GeoTools file not found:" + super.getFilename(), null);
			// CorePlugin.showMessage("Load GIS dataset", "Sorry, can't find the file:\n" +
			// super.getFilename());
		}

		// ok, add ourselves to the map
		if (_myLayer != null) {
			// sort out the visibility
			_myLayer.setVisible(this.getVisible());

			_myLayer.setTitle(super.getName());
			_myMap.addLayer(_myLayer);

		}
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);

		if (_myLayer != null)
			_myLayer.setVisible(visible);
	}
}

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
package org.mwc.cmap.gt2plot.data;

import java.io.File;

import org.eclipse.core.runtime.Status;
import org.geotools.data.DataSourceException;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.ExternallyManagedDataLayer;

public abstract class GeoToolsLayer extends ExternallyManagedDataLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NATURAL_EARTH = "Natural Earth";
	
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

	public GeoToolsLayer(final String dataType, final String layerName, final String fileName)
	{
		super(dataType, layerName, fileName);
	}

	@Override
	protected void finalize() throws Throwable
	{
		// ok, ditch the layer
		if (_myLayer != null)
			_myLayer.dispose();

		// and let the parent do it's stuff
		super.finalize();
	}

	@Override
	public void setVisible(final boolean visible)
	{
		super.setVisible(visible);

		if (_myLayer != null)
			_myLayer.setVisible(visible);
	}

	/**
	 * forget about any existing map/layer
	 * 
	 */
	public void clearMap()
	{
		if (_myLayer != null)
		{
			// remove ourselves from the map
			if (_myMap != null)
			{
				_myMap.removeLayer(_myLayer);
			}
			_myLayer.dispose();
			_myLayer = null;
		}
		_myMap = null;
	}

	/**
	 * tell this layer where to plot itself
	 * 
	 * @param map
	 */
	public void setMap(final MapContent map)
	{
		clearMap();

		// remember the map
		_myMap = map;

		// read ourselves in
		final File openFile = new File(super.getFilename());
		if (openFile != null && openFile.exists())
		{
			_myLayer = loadLayer(openFile);
		}
		else
		{
			CorePlugin.logError(Status.WARNING, "GeoTools file not found:" + super.getFilename(), null);
			CorePlugin.showMessage("Load GIS dataset", "Sorry, can't find the file:\n" + super.getFilename());
		}

		// ok, add ourselves to the map
		if (_myLayer != null)
		{
			// sort out the visibility
			_myLayer.setVisible(this.getVisible());

			_myLayer.setTitle(super.getName());
			_myMap.addLayer(_myLayer);

		}
	}

	public Layer getLayer()
	{
		return _myLayer;
	}

	/**
	 * load data from the specific file, return it as a GeoTools layer
	 * 
	 * @param openFile
	 *          the file to open
	 * @return the layer to load into a GeoTools object
	 */
	abstract protected Layer loadLayer(File openFile);

}

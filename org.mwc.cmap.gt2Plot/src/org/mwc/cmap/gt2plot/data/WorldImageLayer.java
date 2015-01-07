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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.DataSourceException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.image.WorldImageReader;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.Property;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;

import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class WorldImageLayer extends GeoToolsLayer
{
	public final static String RASTER_FILE = "rasterExtents_ARCS_Export";

	public WorldImageLayer(final String layerName, final String fileName)
	{
		super(ChartBoundsWrapper.WORLDIMAGE_TYPE, layerName, fileName);
	}

	protected Layer loadLayer(final File openFile)
	{
		Layer res = null;
		AbstractGridCoverage2DReader tiffReader = null;
		try
		{

			final String nameWithoutExtention = FileUtilities
					.getNameWithoutExtention(openFile);
			final File twfFile = new File(openFile.getParentFile(), nameWithoutExtention
					+ ".tfw");
			if (twfFile.exists())
			{
				tiffReader = new WorldImageReader(openFile);
			}
			else
			{
				tiffReader = new GeoTiffReader(openFile);
			}

		}
		catch (final DataSourceException e)
		{
			e.printStackTrace();
		}

		// WorldImageFormat format = new WorldImageFormat();
		// AbstractGridFormat format = GridFormatFinder.findFormat(openFile);
		// AbstractGridCoverage2DReader tiffReader = format.getReader(openFile);
		if (tiffReader != null)
		{
			final StyleFactoryImpl sf = new StyleFactoryImpl();
			final RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
			final Style defaultStyle = SLD.wrapSymbolizers(symbolizer);

			final GeneralParameterValue[] params = null;

			res = new GridReaderLayer(tiffReader, defaultStyle, params);

		}
		return res;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// public static MWC.GUI.Layer read(String fileName)
	// {
	//
	// MWC.GUI.Layer res = null;
	// File openFile = new File(fileName);
	// if (openFile != null && openFile.exists())
	// {
	// // sort out the name of the map
	// String coverageName = fileName;
	// final int dotIndex = coverageName.lastIndexOf(".");
	// coverageName = (dotIndex == -1) ? coverageName : coverageName.substring(
	// 0, dotIndex);
	// final int pathIndex = coverageName.lastIndexOf(File.separator);
	// if (pathIndex > 0)
	// coverageName = coverageName.substring(pathIndex + 1,
	// coverageName.length());
	//
	// // also create a layer wrapper
	// res = new ExternallyManagedDataLayer(ChartBoundsWrapper.WORLDIMAGE_TYPE,
	// coverageName, fileName);
	// }
	// return res;
	//
	// }

	public static class RasterExtentHelper
	{
		public static MWC.GUI.Layer loadRasters(final String fileName, final Layers parent)
		{
			// and sort out the parent folder name
			final File theFile = new File(fileName);
			final String parentPath = theFile.getParent();
			final int slasher = parentPath.lastIndexOf(File.separator);
			final String folderName = parentPath.substring(slasher + 1);

			final MWC.GUI.Layer res = new ChartFolio(false, Color.red);
			res.setName("Chart lib:" + folderName);

			loadExtentsFor(res, fileName, parent);

			return res;
		}

		protected static void loadExtentsFor(final MWC.GUI.Layer extents,
				final String fileName, final Layers parent)
		{
			// ok, get the extents for this file

			// ok, populate from this file
			FileDataStore store;
			try
			{
				final File openFile = new File(fileName);
				store = FileDataStoreFinder.getDataStore(openFile);
				final SimpleFeatureSource featureSource = store.getFeatureSource();

				// sort out the parent path
				final String parentPath = openFile.getParent();

				// hey, can we parse it?
				final SimpleFeatureCollection fs = featureSource.getFeatures();
				final SimpleFeatureIterator fiter = fs.features();
				while (fiter.hasNext())
				{
					// get ready to load this feature
					WorldArea area = null;
					String name = null;

					final SimpleFeatureImpl thisF = (SimpleFeatureImpl) fiter.next();
					final Collection<? extends Property> values = thisF.getValue();
					final Iterator<? extends Property> iter = values.iterator();
					while (iter.hasNext())
					{
						final Property thisProp = iter.next();
						final String propName = thisProp.getName().toString();

						// is this the geometry?
						if (propName.equals("the_geom"))
						{
							area = getGeometry(thisProp.getValue());
						}
						else if (propName.equals("Name"))
						{
							name = thisProp.getValue().toString();
						}
					}

					// are we done?
					if ((area != null) && (name != null))
					{
						// generate the filename
						final String path = parentPath + File.separator + name + ".tif";

						final ChartBoundsWrapper cw = new ChartBoundsWrapper(name,
								area.getTopLeft(), area.getBottomRight(), Color.red, path);
						cw.setLayers(parent);
						cw.setLabelLocation(LocationPropertyEditor.CENTRE);
						cw.setLabelVisible(false);
						extents.add(cw);
					}
				}
				// ok, lastly check we have the correct projection files
				ProjSidecarGenerator.addPrj(parentPath, "EPSG:4326");

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			catch (final FactoryException e)
			{
				e.printStackTrace();
			}

		}

		private static WorldArea getGeometry(final Object value)
		{
			WorldArea res = null;
			if (value instanceof MultiPolygon)
			{
				final MultiPolygon mp = (MultiPolygon) value;
				final Geometry bound = mp.getBoundary();
				final Coordinate[] coords = bound.getCoordinates();
				if (coords != null)
				{

					for (int i = 0; i < coords.length; i++)
					{
						final Coordinate coordinate = coords[i];
						final double zDepth;
						if (Double.isNaN(coordinate.z))
							zDepth = 0;
						else
							zDepth = coordinate.z;
						final WorldLocation newL = new WorldLocation(coordinate.y, coordinate.x,
								zDepth);
						if (res == null)
							res = new WorldArea(newL, newL);
						else
							res.extend(newL);
					}
				}
			}
			return res;
		}
	}
}

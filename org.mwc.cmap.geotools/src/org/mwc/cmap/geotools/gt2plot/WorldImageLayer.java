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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.Property;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class WorldImageLayer extends GeoToolsLayer {
	public static class RasterExtentHelper {
		private static WorldArea getGeometry(final Object value) {
			WorldArea res = null;
			if (value instanceof MultiPolygon) {
				final MultiPolygon mp = (MultiPolygon) value;
				final Geometry bound = mp.getBoundary();
				final Coordinate[] coords = bound.getCoordinates();
				if (coords != null) {

					for (int i = 0; i < coords.length; i++) {
						final Coordinate coordinate = coords[i];
						final double zDepth;
						if (Double.isNaN(coordinate.z))
							zDepth = 0;
						else
							zDepth = coordinate.z;
						final WorldLocation newL = new WorldLocation(coordinate.y, coordinate.x, zDepth);
						if (res == null)
							res = new WorldArea(newL, newL);
						else
							res.extend(newL);
					}
				}
			}
			return res;
		}

		protected static void loadExtentsFor(final MWC.GUI.Layer extents, final String fileName, final Layers parent) {
			// ok, get the extents for this file

			// ok, populate from this file
			FileDataStore store;
			try {
				final File openFile = new File(fileName);
				store = FileDataStoreFinder.getDataStore(openFile);
				final SimpleFeatureSource featureSource = store.getFeatureSource();

				// sort out the parent path
				final String parentPath = openFile.getParent();

				// hey, can we parse it?
				final SimpleFeatureCollection fs = featureSource.getFeatures();
				final SimpleFeatureIterator fiter = fs.features();
				while (fiter.hasNext()) {
					// get ready to load this feature
					WorldArea area = null;
					String name = null;

					final SimpleFeatureImpl thisF = (SimpleFeatureImpl) fiter.next();
					final Collection<? extends Property> values = thisF.getValue();
					final Iterator<? extends Property> iter = values.iterator();
					while (iter.hasNext()) {
						final Property thisProp = iter.next();
						final String propName = thisProp.getName().toString();

						// is this the geometry?
						if (propName.equals("the_geom")) {
							area = getGeometry(thisProp.getValue());
						} else if (propName.equals("Name")) {
							name = thisProp.getValue().toString();
						}
					}

					// are we done?
					if ((area != null) && (name != null)) {
						// generate the filename
						final String path = parentPath + File.separator + name + ".tif";

						final ChartBoundsWrapper cw = new ChartBoundsWrapper(name, area.getTopLeft(),
								area.getBottomRight(), Color.red, path);
						cw.setLayers(parent);
						cw.setLabelLocation(LocationPropertyEditor.CENTRE);
						cw.setLabelVisible(false);
						extents.add(cw);
					}
				}
				// ok, lastly check we have the correct projection files
				ProjSidecarGenerator.addPrj(parentPath, "EPSG:4326");

			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final FactoryException e) {
				e.printStackTrace();
			}

		}

		public static MWC.GUI.Layer loadRasters(final String fileName, final Layers parent) {
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
	}

	public final static String RASTER_FILE = "rasterExtents_ARCS_Export";

	/**
	 * the world file suffixes we accept alongside a plain (non-GeoTiff) image
	 */
	private final static String[] WORLD_FILE_EXTENSIONS = { ".tfw", ".tifw", ".wld" };

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * flag for if file exists
	 * 
	 */
	private final boolean _fileExists;

	/**
	 * the outer bounds of this image, in degrees. Cached, since working it out
	 * involves a coordinate transform, and we get asked for it on each repaint.
	 * Cleared when we drop our GeoTools layer.
	 */
	private transient WorldArea _cachedBounds;

	/**
	 * whether the image we loaded is actually positioned on the earth. If it
	 * isn't, we mustn't offer any bounds - GeoTools will have fallen back to the
	 * identity transform, and those coordinates would drag the whole plot off to
	 * an arbitrary spot near (0,0).
	 */
	private transient boolean _isGeoReferenced;

	public WorldImageLayer(final String layerName, final String fileName) {
		super(ChartBoundsWrapper.WORLDIMAGE_TYPE, layerName, fileName);

		final File testIfExists = new File(fileName);
		_fileExists = testIfExists.exists();
	}

	@Override
	public void clearMap() {
		// we're losing our GeoTools layer, so ditch what we learned from it
		_cachedBounds = null;
		_isGeoReferenced = false;

		super.clearMap();
	}

	/**
	 * provide the outer bounds of this image, in degrees.
	 *
	 * Note: without this, a GeoTiff contributes no bounds to the plot (the parent
	 * implementation works through our child plottables, and we don't have any).
	 * That leaves Debrief unable to navigate to the image - so "fit to window",
	 * and the rescale that runs after a file is dropped onto the plot, both
	 * ignore it, and the image ends up off-screen and unpainted.
	 */
	@Override
	public WorldArea getBounds() {
		// do we already know the answer?
		if (_cachedBounds != null) {
			return _cachedBounds;
		}

		// have we been loaded into a map yet?
		if (_myLayer == null) {
			return null;
		}

		// do we know where the image belongs? If not, the coordinates GeoTools
		// gives us are pixel counts, not a position - so they're no use here
		if (!_isGeoReferenced) {
			return null;
		}

		final ReferencedEnvelope bounds = _myLayer.getBounds();

		// is it any use to us?
		if (bounds == null || bounds.isEmpty() || bounds.getCoordinateReferenceSystem() == null) {
			return null;
		}

		try {
			// the image will be in its own projection, get it into degrees. Note:
			// we use WGS84 rather than decoding EPSG:4326, since WGS84 is always
			// longitude-first - whatever the axis-order setting happens to be.
			final ReferencedEnvelope degs = bounds.transform(DefaultGeographicCRS.WGS84, true);

			final WorldLocation tl = new WorldLocation(degs.getMaxY(), degs.getMinX(), 0d);
			final WorldLocation br = new WorldLocation(degs.getMinY(), degs.getMaxX(), 0d);

			_cachedBounds = new WorldArea(tl, br);
		} catch (final TransformException e) {
			Application.logError2(ToolParent.WARNING, "Failed to convert bounds for GeoTiff:" + getFilename(), e);
		} catch (final FactoryException e) {
			Application.logError2(ToolParent.WARNING, "Failed to convert bounds for GeoTiff:" + getFilename(), e);
		}

		return _cachedBounds;
	}

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

	@Override
	protected Layer loadLayer(final File openFile) {
		Layer res = null;
		AbstractGridCoverage2DReader tiffReader = null;
		final AbstractGridFormat format = GridFormatFinder.findFormat(openFile);
		final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

		GeoToolsLayer.registerTifUrlServiceProvider();

		try {
			tiffReader = format.getReader(openFile, hints);
		} catch (final RuntimeException e) {
			// GeoTools throws if the file isn't a grid format at all, and also if it
			// can't make sense of the georeferencing. Don't let that escape - it
			// would take down the whole file-drop, with just a stack trace to go on.
			Application.logError2(ToolParent.ERROR, "Unable to read image file:" + openFile.getAbsolutePath(), e);
		}

		/*
		 * try {
		 *
		 * final String nameWithoutExtention = FileUtilities
		 * .getNameWithoutExtention(openFile); final File twfFile = new
		 * File(openFile.getParentFile(), nameWithoutExtention + ".tfw"); if
		 * (twfFile.exists()) { tiffReader = new WorldImageReader(openFile); } else {
		 * tiffReader = new GeoTiffReader(openFile); }
		 *
		 * } catch (final DataSourceException e) { e.printStackTrace(); }
		 */
		// WorldImageFormat format = new WorldImageFormat();
		// AbstractGridFormat format = GridFormatFinder.findFormat(openFile);
		// AbstractGridCoverage2DReader tiffReader = format.getReader(openFile);
		if (tiffReader != null) {
			// is the image actually positioned on the earth?
			_isGeoReferenced = isGeoReferenced(openFile, tiffReader);

			if (!_isGeoReferenced) {
				// GeoTools falls back to the identity transform for an image with no
				// georeferencing - which silently drops it near (0,0), sized in
				// degrees-per-pixel. Say so, rather than leave the user guessing.
				Application.logError2(ToolParent.WARNING, "Image has no georeferencing, so Debrief can't"
						+ " position it. Expected a GeoTiff, or an image with a world file:"
						+ openFile.getAbsolutePath(), null);
			}

			final StyleFactoryImpl sf = new StyleFactoryImpl();
			final RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
			final Style defaultStyle = SLD.wrapSymbolizers(symbolizer);

			final GeneralParameterValue[] params = null;

			res = new GridReaderLayer(tiffReader, defaultStyle, params);
		}
		return res;
	}

	/**
	 * is this image positioned on the earth?
	 *
	 * There are two ways for it to be: a GeoTiff carries its coordinates and
	 * projection inside the file, otherwise we're looking at a plain image that
	 * relies on an accompanying world file.
	 */
	private static boolean isGeoReferenced(final File openFile, final AbstractGridCoverage2DReader reader) {
		// a GeoTiff has the georeferencing written inside it
		if (reader instanceof GeoTiffReader) {
			return true;
		}

		// nope, so we need a world file sitting next to the image
		final String name = openFile.getName();
		final int lastDot = name.lastIndexOf('.');
		final String stem = lastDot > 0 ? name.substring(0, lastDot) : name;
		final File parent = openFile.getParentFile();

		for (final String extension : WORLD_FILE_EXTENSIONS) {
			// check both cases, since we also accept images with an upper-case suffix
			if (new File(parent, stem + extension).exists()
					|| new File(parent, stem + extension.toUpperCase()).exists()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return _fileExists ? super.getName() : super.getName() + " (Not found)";
	}
}

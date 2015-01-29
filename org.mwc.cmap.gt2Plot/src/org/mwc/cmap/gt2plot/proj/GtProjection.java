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
package org.mwc.cmap.gt2plot.proj;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gt2plot.GtActivator;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GtProjection extends PlainProjection implements GeoToolsHandler
{

	private static final String WORLD_PROJECTION = "EPSG:3395"; // 3395 for Mercator proj
	private static final String DATA_PROJECTION = "EPSG:4326";
	private CoordinateReferenceSystem _worldCoords;
	protected MathTransform _degs2metres;

	private WorldLocation _relativeCentre = null;

	private final MapContent _map;
	private final MapViewport _view;
	private WorldArea _oldDataArea;

	// to reduce object creation these three objects are created in advance, and
	// reused
	private final DirectPosition2D _workDegs;
	private final DirectPosition2D _workMetres;
	private final DirectPosition2D _workScreen;

	public GtProjection()
	{
		super("GeoTools");

		// initialise our working data stores
		_workDegs = new DirectPosition2D();
		_workMetres = new DirectPosition2D();
		_workScreen = new DirectPosition2D();

		_map = new MapContent();
		_view = _map.getViewport();

		// set the aspect radio matching to true. The default
		// value for this was false - but when we did fit to
		// window, it wasn't putting the specified area in the centre of the shape
		_view.setMatchingAspectRatio(true);

		// sort out the degs to m transform
		try
		{
			// we'll tell GeoTools to use the projection that's used by most of our
			// charts,
			// so that the chart will be displayed undistorted
			_worldCoords = CRS.decode(WORLD_PROJECTION);

			// we also need a way to convert a location in degrees to that used by
			// the charts (metres)
			final CoordinateReferenceSystem worldDegs = CRS.decode(DATA_PROJECTION);
			_degs2metres = CRS.findMathTransform(worldDegs, _worldCoords);
		}
		catch (final NoSuchAuthorityCodeException e)
		{
			GtActivator
					.logError(
							Status.ERROR,
							"Can't find the requested authority whilst trying to create CRS transform",
							e);
		}
		catch (final FactoryException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst trying to create CRS transform", e);
		}

		_view.setCoordinateReferenceSystem(_worldCoords);

		// SPECIAL HANDLING: this is the kludge to ensure the aspect ratio is kept
		// constant
		_view.setMatchingAspectRatio(true);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Point toScreen(final WorldLocation val)
	{

		Point res = null;

		// special handling: if we're in a relative plotting mode, we need to shift
		// the projection. We're choosing to defer handling of this instance until
		// we're actually
		// plotting the data.
		// - we cache the current relative centre, and only bother shifting the
		// transform if it's a new centre.

		// right, quick check. are we in a primary centred mode?
		if (super.getNonStandardPlotting() && super.getPrimaryCentred())
		{
			final WorldLocation loc = super._relativePlotter.getLocation();

			// do we have a location for this plotter? We may not have...
			if (loc != null)
			{
				// have we got a 'remembered data area'?
				if (_oldDataArea == null)
				{
					// remember the current data area
					_oldDataArea = super.getDataArea();
				}

				// ok, handle the changes
				if (loc != _relativeCentre)
				{
					// store the new centre
					_relativeCentre = loc;

					// set the centre of the new data area
					final WorldArea newArea = new WorldArea(super.getDataArea());

					// shift it to our current centre
					newArea.setCentre(_relativeCentre);

					// and store this area
					this.mySetDataArea(newArea);
				}
			}
		}
		else
		{
			// we're not in primary centred mode. do we need to restore an old data
			// area?
			if (_oldDataArea != null)
			{
				// ok, re-instate that old area
				this.mySetDataArea(_oldDataArea);

				// and clear the flag
				_oldDataArea = null;
			}
		}

		// and now for the actual projection bit
		_workDegs.setLocation(val.getLong(), val.getLat());
		try
		{
			_degs2metres.transform(_workDegs, _workMetres);

			// now got to screen
			_view.getWorldToScreen().transform(_workMetres, _workScreen);

			// output the results
			res = new Point((int) _workScreen.getCoordinate()[0],
					(int) _workScreen.getCoordinate()[1]);
		}
		catch (final MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to convert to screen coords", e);
		}
		catch (final TransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to convert to screen coords", e);
		}

		return res;
	}

	@Override
	public WorldLocation toWorld(final Point val)
	{
		WorldLocation res = null;
		_workScreen.setLocation(val.x, val.y);

		try
		{
			// now got to screen
			_view.getScreenToWorld().transform(_workScreen, _workMetres);
			_degs2metres.inverse().transform(_workMetres, _workDegs);
			res = new WorldLocation(_workDegs.getCoordinate()[1],
					_workDegs.getCoordinate()[0], 0);
		}
		catch (final MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to set convert to world coords", e);
		}
		catch (final org.opengis.referencing.operation.NoninvertibleTransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing screen to world", e);
		}
		catch (final TransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing screen to world", e);
		}
		return res;
	}

	private DirectPosition2D getDirectPositionOf(final WorldLocation x)
			throws MismatchedDimensionException, TransformException
	{
		final DirectPosition2D mapPos = new DirectPosition2D(x.getLong(),
				x.getLat());
		final DirectPosition2D mapM = new DirectPosition2D();
		_degs2metres.transform(mapPos, mapM);
		return mapM;
	}

	private WorldArea convertToWorldArea(final Envelope2D newMapArea)
			throws MismatchedDimensionException,
			org.opengis.referencing.operation.NoninvertibleTransformException,
			TransformException
	{
		// convert back to friendly units
		final DirectPosition2D tlDegs = new DirectPosition2D();
		final DirectPosition2D brDegs = new DirectPosition2D();

		_degs2metres.inverse().transform(newMapArea.getLowerCorner(), brDegs);
		_degs2metres.inverse().transform(newMapArea.getUpperCorner(), tlDegs);

		final WorldLocation tl = new WorldLocation(brDegs.y, brDegs.x, 0d);
		final WorldLocation br = new WorldLocation(tlDegs.y, tlDegs.x, 0d);
		return new WorldArea(tl, br);
	}

	@Override
	public void zoom(double scaleVal)
	{
		if (scaleVal == 0)
			scaleVal = 1;
		final Dimension paneArea = super.getScreenArea();
		final WorldArea dataArea = super.getDataArea();
		if (dataArea != null)
		{
			final WorldLocation centre = super.getDataArea().getCentre();

			try
			{
				final DirectPosition2D mapM = getDirectPositionOf(centre);

				if (_view.getWorldToScreen() == null)
					return;

				double scale = _view.getWorldToScreen().getScaleX();
				scale = Math.min(1000, scale);
				double newScale = scale / scaleVal;

				final DirectPosition2D corner = new DirectPosition2D(mapM.getX() - 0.5d
						* paneArea.width / newScale, mapM.getY() + 0.5d * paneArea.height
						/ newScale);

				final Envelope2D newMapArea = new Envelope2D();
				newMapArea.setFrameFromCenter(mapM, corner);

				final WorldArea newArea = convertToWorldArea(newMapArea);
				newArea.normalise();

				setDataArea(newArea);

			}
			catch (final MismatchedDimensionException e)
			{
				GtActivator.logError(Status.ERROR,
						"Unexpected problem whilst performing zoom", e);
			}
			catch (final org.opengis.referencing.operation.NoninvertibleTransformException e)
			{
				GtActivator.logError(Status.ERROR,
						"Unable to do inverse transform in zoom", e);
			}
			catch (final TransformException e)
			{
				GtActivator.logError(Status.ERROR,
						"Unexpected problem whilst performing", e);
			}

		}

	}

	@Override
	public void zoom(final double scaleVal, final WorldArea area)
	{
		if (area == null)
		{
			zoom(scaleVal);
			return;
		}

		final WorldArea dataArea = super.getDataArea();
		if (dataArea == null)
		{
			return;
		}

		final Dimension paneArea = super.getScreenArea();

		try
		{
			if (_view.getWorldToScreen() == null)
				return;

			final DirectPosition2D desiredCenter = getDirectPositionOf(area
					.getCentre());
			final DirectPosition2D actualCenter = getDirectPositionOf(super
					.getDataArea().getCentre());
			final double deltaX = actualCenter.getX() - desiredCenter.getX();
			final double deltaY = actualCenter.getY() - desiredCenter.getY();

			double scale = _view.getWorldToScreen().getScaleX();
			scale = Math.min(1000, scale);
			double newScale = scale;

			if (scaleVal != 0)
				newScale = scale / scaleVal;

			final DirectPosition2D corner = new DirectPosition2D(desiredCenter.getX()
					- 0.5d * paneArea.width / newScale, desiredCenter.getY() + 0.5d
					* paneArea.height / newScale);

			final Envelope2D newMapArea = new Envelope2D();
			// scale
			newMapArea.setFrameFromCenter(desiredCenter, corner);

			final double height = newMapArea.getHeight();
			final double width = newMapArea.getWidth();
			// translate
			newMapArea.setFrameFromDiagonal(newMapArea.getBounds().x + deltaX
					* scaleVal, newMapArea.getBounds().y + deltaY * scaleVal,
					newMapArea.getBounds().x + width + deltaX * scaleVal,
					newMapArea.getBounds().y + height + deltaY * scaleVal);

			final WorldArea newArea = convertToWorldArea(newMapArea);
			newArea.normalise();

			setDataArea(newArea);

		}
		catch (MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing zoom", e);
		}
		catch (org.opengis.referencing.operation.NoninvertibleTransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unable to do inverse transform in zoom", e);
		}
		catch (TransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing", e);
		}

	}

	@Override
	public void setScreenArea(Dimension theArea)
	{
		if (theArea.equals(super.getScreenArea()))
			return;

		super.setScreenArea(theArea);

		final java.awt.Rectangle screenArea = new java.awt.Rectangle(0, 0,
				theArea.width, theArea.height);
		_view.setScreenArea(screenArea);
	}

	@Override
	public void setDataArea(final WorldArea theArea)
	{
		if (theArea == null)
		{
			CorePlugin.logError(Status.WARNING, "GtProjection received null in setDataArea - maintainer to be informed", null, true);
			return;
		}
		// trim the area to sensible bounds
		theArea.trim();

		mySetDataArea(theArea);

		// and store it in the parent;
		super.setDataArea(theArea);
	}

	private void mySetDataArea(final WorldArea theArea)
	{
		// double-check we're not already ste to this
		if (theArea.equals(super.getDataArea()))
		{
			System.err.println("OVER-RIDING EXISTING AREA - TRAP THIS INSTANCE");
			return;
		}

		// trim the coordinates
		gtTrim(theArea);

		final WorldLocation tl = theArea.getTopLeft();
		final WorldLocation br = theArea.getBottomRight();

		final DirectPosition2D tlDegs = new DirectPosition2D(tl.getLong(),
				tl.getLat());
		final DirectPosition2D brDegs = new DirectPosition2D(br.getLong(),
				br.getLat());

		final DirectPosition2D tlM = new DirectPosition2D();
		final DirectPosition2D brM = new DirectPosition2D();

		try
		{
			_degs2metres.transform(tlDegs, tlM);
			_degs2metres.transform(brDegs, brM);

			// put the coords into an envelope
			final Envelope2D env = new Envelope2D(brM, tlM);
			final ReferencedEnvelope rEnv = new ReferencedEnvelope(env, _worldCoords);
			_view.setBounds(rEnv);
		}
		catch (final ProjectionException e)
		{
			CorePlugin.logError(Status.ERROR,
					"trouble with proj, probably zoomed out too far", e);
		}
		catch (final MismatchedDimensionException e)
		{
			CorePlugin.logError(Status.ERROR, "unknown trouble with proj", e);
		}
		catch (final TransformException e)
		{
			CorePlugin.logError(Status.ERROR, "unknown trouble with proj", e);
		}
	}

	private void gtTrim(final WorldArea theArea)
	{
		gtTrim(theArea.getTopLeft());
		gtTrim(theArea.getBottomRight());
	}

	private void gtTrim(final WorldLocation loc)
	{
		loc.setLat(Math.min(loc.getLat(), 89.9999));
		loc.setLat(Math.max(loc.getLat(), -89.9999));

		loc.setLong(Math.min(loc.getLong(), 179.999));
		loc.setLong(Math.max(loc.getLong(), -179.999));
	}

	public MapContent getMapContent()
	{
		return _map;
	}

	public void addGeoToolsLayer(final ExternallyManagedDataLayer gt)
	{
		final GeoToolsLayer geoLayer = (GeoToolsLayer) gt;
		geoLayer.setMap(_map);
	}

	/**
	 * how many layers do we have loaded?
	 * 
	 * @return
	 */
	public int numLayers()
	{
		return _map.layers().size();
	}

	/**
	 * whether the geotools layers overlap with the specified area
	 * 
	 * @param area
	 * @return
	 */
	public boolean layersOverlapWith(final WorldArea area)
	{
		final WorldLocation tl = area.getTopLeft();
		final WorldLocation br = area.getBottomRight();

		final DirectPosition2D tlDegs = new DirectPosition2D(tl.getLong(),
				tl.getLat());
		final DirectPosition2D brDegs = new DirectPosition2D(br.getLong(),
				br.getLat());

		final DirectPosition2D tlM = new DirectPosition2D();
		final DirectPosition2D brM = new DirectPosition2D();

		try
		{
			_degs2metres.transform(tlDegs, tlM);
			_degs2metres.transform(brDegs, brM);

			// put the coords into an envelope
			final Envelope2D env = new Envelope2D(brM, tlM);
			final BoundingBox other = new ReferencedEnvelope(env, _worldCoords);

			final Iterator<Layer> layers = _map.layers().iterator();
			while (layers.hasNext())
			{
				final Layer layer = (Layer) layers.next();
				if (layer.isVisible())
				{
					final ReferencedEnvelope thisBounds = layer.getBounds();

					if (thisBounds != null)
					{
						// right, now the painful bit of converting the layers
						ReferencedEnvelope newBounds;
						try
						{
							newBounds = thisBounds.transform(
									other.getCoordinateReferenceSystem(), false);
						}
						catch (TransformException e)
						{
							return true;
						}

						if (newBounds.intersects(other))
							return true;
					}
					else
					{
						CorePlugin.logError(Status.WARNING, "GtProjection overlap. Layer has no bounds:"+ layer.getTitle(), null);
					}
				}
			}
		}
		catch (final MismatchedDimensionException e)
		{
			CorePlugin.logError(Status.ERROR,
					"unknown dimension trouble with getting bounds", e);
		}
		catch (final TransformException e)
		{
			CorePlugin.logError(Status.ERROR,
					"unknown transform trouble with getting bounds", e);
		}
		catch (final FactoryException e)
		{
			CorePlugin.logError(Status.ERROR,
					"unknown factory trouble with getting bounds", e);
		}

		return false;
	}

	public static class TestProj extends TestCase
	{
		public void testOne() throws NoSuchAuthorityCodeException,
				FactoryException, NoninvertibleTransformException
		{
			final MapContent mc = new MapContent();

			// set a coordinate reference system
			final CoordinateReferenceSystem crs = CRS.decode(DATA_PROJECTION);
			mc.getViewport().setCoordinateReferenceSystem(crs);

			// set a data area
			final DirectPosition2D tlDegs = new DirectPosition2D(5, 1);
			final DirectPosition2D brDegs = new DirectPosition2D(1, 5);
			final Envelope2D env = new Envelope2D(tlDegs, brDegs);
			final ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
			mc.getViewport().setBounds(rEnv);

			// set a screen area
			mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 400));

			// sort out the aspect ration
			mc.getViewport().setMatchingAspectRatio(true);

			// create a point to test
			final DirectPosition2D degs = new DirectPosition2D(5, 4);

			// and results object
			final DirectPosition2D pixels = new DirectPosition2D();
			final DirectPosition2D rDegs = new DirectPosition2D();

			// transform the test point
			mc.getViewport().getWorldToScreen().transform(degs, pixels);

			System.out.println("pixels:" + pixels);
			assertEquals("correct x", 600, (int) pixels.x);
			assertEquals("correct y", 600, (int) pixels.x);

			// and the reverse transform
			mc.getViewport().getWorldToScreen().inverseTransform(pixels, rDegs);

			System.out.println("degs:" + rDegs);
			assertEquals("correct x", 5, (int) rDegs.x);
			assertEquals("correct y", 4, (int) rDegs.y);
		}

		public void testTwo() throws NoSuchAuthorityCodeException,
				FactoryException, NoninvertibleTransformException
		{
			final MapContent mc = new MapContent();

			// set a coordinate reference system
			final CoordinateReferenceSystem crs = CRS.decode(DATA_PROJECTION);
			mc.getViewport().setCoordinateReferenceSystem(crs);

			// set a data area
			final DirectPosition2D tlDegs = new DirectPosition2D(45, -5);
			final DirectPosition2D brDegs = new DirectPosition2D(41, -1);
			final Envelope2D env = new Envelope2D(tlDegs, brDegs);
			final ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
			mc.getViewport().setBounds(rEnv);

			// set a screen area
			mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 200));

			// sort out the aspect ration
			mc.getViewport().setMatchingAspectRatio(true);

			// try with series of points
			System.out.println("test 2:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -4), null));
			System.out.println("test 2:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(44, -4), null));
			System.out.println("test 2:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(43, -4), null));
			System.out.println("test 2:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(42, -4), null));
			System.out.println("test 2:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(41, -4), null));

			// try with series of points
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -5), null));
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -4), null));
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -3), null));
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -2), null));
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, -1), null));
			System.out.println("test 3:"
					+ mc.getViewport().getWorldToScreen()
							.transform(new DirectPosition2D(45, 0), null));

		}

	}

	@Override
	public void dispose()
	{
		if (_map != null)
		{
			_map.dispose();
		}
	}

}

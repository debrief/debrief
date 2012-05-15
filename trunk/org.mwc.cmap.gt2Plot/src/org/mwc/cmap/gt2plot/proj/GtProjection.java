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

		// sort out the degs to m transform
		try
		{
			// we'll tell GeoTools to use the projection that's used by most of our
			// charts,
			// so that the chart will be displayed undistorted
			_worldCoords = CRS.decode("EPSG:3395");

			// we also need a way to convert a location in degrees to that used by
			// the charts (metres)
			CoordinateReferenceSystem worldDegs = CRS.decode("EPSG:4326");
			_degs2metres = CRS.findMathTransform(worldDegs, _worldCoords);
		}
		catch (NoSuchAuthorityCodeException e)
		{
			GtActivator
					.logError(
							Status.ERROR,
							"Can't find the requested authority whilst trying to create CRS transform",
							e);
		}
		catch (FactoryException e)
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
	public Point toScreen(WorldLocation val)
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
			WorldLocation loc = super._relativePlotter.getLocation();

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
					WorldArea newArea = new WorldArea(super.getDataArea());

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
		catch (MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to convert to screen coords", e);
		}
		catch (TransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to convert to screen coords", e);
		}

		return res;
	}

	@Override
	public WorldLocation toWorld(Point val)
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
		catch (MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR,
					"Whilst trying to set convert to world coords", e);
		}
		catch (org.opengis.referencing.operation.NoninvertibleTransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing screen to world", e);
		}
		catch (TransformException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst performing screen to world", e);
		}
		return res;
	}

	@Override
	public void zoom(double scaleVal)
	{
		if (scaleVal == 0)
			scaleVal = 1;
		Dimension paneArea = super.getScreenArea();
		WorldArea dataArea = super.getDataArea();
		if (dataArea != null)
		{
			WorldLocation centre = super.getDataArea().getCentre();
			DirectPosition2D mapPos = new DirectPosition2D(centre.getLong(),
					centre.getLat());

			DirectPosition2D mapM = new DirectPosition2D();
			try
			{
				_degs2metres.transform(mapPos, mapM);

				if (_view.getWorldToScreen() == null)
					return;

				double scale = _view.getWorldToScreen().getScaleX();
				scale = Math.min(1000, scale);
				double newScale = scale / scaleVal;

				DirectPosition2D corner = new DirectPosition2D(mapM.getX() - 0.5d
						* paneArea.width / newScale, mapM.getY() + 0.5d * paneArea.height
						/ newScale);

				Envelope2D newMapArea = new Envelope2D();
				newMapArea.setFrameFromCenter(mapM, corner);

				// convert back to friendly units
				DirectPosition2D tlDegs = new DirectPosition2D();
				DirectPosition2D brDegs = new DirectPosition2D();

				_degs2metres.inverse().transform(newMapArea.getLowerCorner(), brDegs);
				_degs2metres.inverse().transform(newMapArea.getUpperCorner(), tlDegs);

				WorldLocation tl = new WorldLocation(brDegs.y, brDegs.x, 0d);
				WorldLocation br = new WorldLocation(tlDegs.y, tlDegs.x, 0d);
				WorldArea newArea = new WorldArea(tl, br);
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

	}

	@Override
	public void setScreenArea(Dimension theArea)
	{
		if (theArea.equals(super.getScreenArea()))
			return;

		super.setScreenArea(theArea);

		java.awt.Rectangle screenArea = new java.awt.Rectangle(0, 0, theArea.width,
				theArea.height);
		_view.setScreenArea(screenArea);
	}

	@Override
	public void setDataArea(WorldArea theArea)
	{
		// trim the area to sensible bounds
		theArea.trim();

		mySetDataArea(theArea);

		// and store it in the parent;
		super.setDataArea(theArea);
	}

	private void mySetDataArea(WorldArea theArea)
	{
		// double-check we're not already ste to this
		if (theArea.equals(super.getDataArea()))
		{
			System.err.println("OVER-RIDING EXISTING AREA - TRAP THIS INSTANCE");
			return;
		}

		// trim the coordinates
		gtTrim(theArea);	

		WorldLocation tl = theArea.getTopLeft();
		WorldLocation br = theArea.getBottomRight();

		DirectPosition2D tlDegs = new DirectPosition2D(tl.getLong(), tl.getLat());
		DirectPosition2D brDegs = new DirectPosition2D(br.getLong(), br.getLat());

		DirectPosition2D tlM = new DirectPosition2D();
		DirectPosition2D brM = new DirectPosition2D();

		try
		{
			_degs2metres.transform(tlDegs, tlM);
			_degs2metres.transform(brDegs, brM);

			// put the coords into an envelope
			Envelope2D env = new Envelope2D(brM, tlM);
			ReferencedEnvelope rEnv = new ReferencedEnvelope(env, _worldCoords);
			_view.setBounds(rEnv);
		}
		catch (ProjectionException e)
		{
			CorePlugin.logError(Status.ERROR,
					"trouble with proj, probably zoomed out too far", e);
		}
		catch (MismatchedDimensionException e)
		{
			CorePlugin.logError(Status.ERROR, "unknown trouble with proj", e);
		}
		catch (TransformException e)
		{
			CorePlugin.logError(Status.ERROR, "unknown trouble with proj", e);
		}
	}

	private void gtTrim(WorldArea theArea)
	{
		gtTrim(theArea.getTopLeft());
		gtTrim(theArea.getBottomRight());
	}

	private void gtTrim(WorldLocation loc)
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

	public void addGeoToolsLayer(ExternallyManagedDataLayer gt)
	{
		GeoToolsLayer geoLayer = (GeoToolsLayer) gt;
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
	public boolean layersOverlapWith(WorldArea area)
	{
		WorldLocation tl = area.getTopLeft();
		WorldLocation br = area.getBottomRight();

		DirectPosition2D tlDegs = new DirectPosition2D(tl.getLong(), tl.getLat());
		DirectPosition2D brDegs = new DirectPosition2D(br.getLong(), br.getLat());

		DirectPosition2D tlM = new DirectPosition2D();
		DirectPosition2D brM = new DirectPosition2D();

		try
		{
			_degs2metres.transform(tlDegs, tlM);
			_degs2metres.transform(brDegs, brM);

			// put the coords into an envelope
			Envelope2D env = new Envelope2D(brM, tlM);
			BoundingBox other = new ReferencedEnvelope(env, _worldCoords);

			Iterator<Layer> layers = _map.layers().iterator();
			while (layers.hasNext())
			{
				Layer layer = (Layer) layers.next();
				if (layer.isVisible())
				{
					ReferencedEnvelope thisBounds = layer.getBounds();

					// right, now the painful bit of converting the layers
					ReferencedEnvelope newBounds = thisBounds.transform(
							other.getCoordinateReferenceSystem(), false);

					if (newBounds.intersects(other))
						return true;
				}
			}
		}
		catch (MismatchedDimensionException e)
		{
			CorePlugin.logError(Status.ERROR,
					"unknown dimension trouble with getting bounds", e);
		}
		catch (TransformException e)
		{
			CorePlugin.logError(Status.ERROR,
					"unknown transform trouble with getting bounds", e);
		}
		catch (FactoryException e)
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
			MapContent mc = new MapContent();

			// set a coordinate reference system
			CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
			mc.getViewport().setCoordinateReferenceSystem(crs);

			// set a data area
			DirectPosition2D tlDegs = new DirectPosition2D(5, 1);
			DirectPosition2D brDegs = new DirectPosition2D(1, 5);
			Envelope2D env = new Envelope2D(tlDegs, brDegs);
			ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
			mc.getViewport().setBounds(rEnv);

			// set a screen area
			mc.getViewport().setScreenArea(new Rectangle(0, 0, 800, 400));

			// sort out the aspect ration
			mc.getViewport().setMatchingAspectRatio(true);

			// create a point to test
			DirectPosition2D degs = new DirectPosition2D(5, 4);

			// and results object
			DirectPosition2D pixels = new DirectPosition2D();
			DirectPosition2D rDegs = new DirectPosition2D();

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
			MapContent mc = new MapContent();

			// set a coordinate reference system
			CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
			mc.getViewport().setCoordinateReferenceSystem(crs);

			// set a data area
			DirectPosition2D tlDegs = new DirectPosition2D(45, -5);
			DirectPosition2D brDegs = new DirectPosition2D(41, -1);
			Envelope2D env = new Envelope2D(tlDegs, brDegs);
			ReferencedEnvelope rEnv = new ReferencedEnvelope(env, crs);
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

}

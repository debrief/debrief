package org.mwc.cmap.gt2plot.proj;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gt2plot.GtActivator;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import MWC.Algorithms.EarthModel;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class GtProjection extends PlainProjection implements GeoToolsHandler,
		EarthModel
{
	private static CoordinateReferenceSystem _worldDegs;

	protected MathTransform _degs2metres;

	private WorldLocation _relativeCentre = null;

	private final MapContent _map;
	private final MapViewport _view;
	private WorldArea _oldDataArea;

	private CoordinateReferenceSystem _worldMetres;

	public GtProjection()
	{
		super("GeoTools");

		_map = new MapContent();
		_view = _map.getViewport();

		// sort out the degs to m transform

		// we also need a way to convert a location in degrees to that used by
		// the charts (metres)
		try
		{
			_worldMetres = CRS.decode("EPSG:3395");
			_worldDegs = getCRS2();
			_degs2metres = CRS.findMathTransform(_worldDegs, _worldMetres);

			// tell the view what to expect
			_view.setCoordinateReferenceSystem(_worldMetres);
		}
		catch (NoSuchAuthorityCodeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FactoryException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SPECIAL HANDLING: this is the kludge to ensure the aspect ratio is kept
		// constant
		_view.setMatchingAspectRatio(true);

	}

	public static CoordinateReferenceSystem getCRS2()
	{
		if (_worldDegs == null)
			try
			{
				_worldDegs = CRS.decode("EPSG:4326");
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
		return _worldDegs;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 	private void gtTrimLocation(WorldLocation loc)
 	{
		loc.setLat(Math.min(loc.getLat(), 89.999));
		loc.setLat(Math.max(loc.getLat(), -89.999));
 		
		loc.setLong(Math.min(loc.getLong(), 179.999));
		loc.setLong(Math.max(loc.getLong(), -179.999)); 		
 	}
	public void gtTrim(WorldArea area)
	{
		WorldLocation topLeft = area.getTopLeft();
		WorldLocation bottomRight = area.getBottomRight();
		// do it, one corner at a time
		gtTrimLocation(topLeft);
		gtTrimLocation(bottomRight);
		
		// and sort out the other corners
		area.normalise();
	}


	
	@Override
	public Point toScreen(WorldLocation val)
	{

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
		DirectPosition2D degs = new DirectPosition2D(val.getLong(), val.getLat());
		DirectPosition2D metres = new DirectPosition2D();
		DirectPosition2D screen = new DirectPosition2D();
		Point res = null;
		try
		{

			_degs2metres.transform(degs, metres);

			// now got to screen
			_view.getWorldToScreen().transform(metres, screen);

			// output the results
			res = new Point((int) screen.getCoordinate()[0],
					(int) screen.getCoordinate()[1]);
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
		DirectPosition2D screen = new DirectPosition2D(val.x, val.y);
		DirectPosition2D metres = new DirectPosition2D();
		DirectPosition2D degs = new DirectPosition2D();

		try
		{
			// now got to screen
			_view.getScreenToWorld().transform(screen, metres);
			_degs2metres.inverse().transform(metres, degs);
			res = new WorldLocation(degs.getCoordinate()[1], degs.getCoordinate()[0],
					0);
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
			return;
		// scaleVal = 1;
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

				System.err.println("zoom, about to set:" + newArea);
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
		System.out.println("new area   :" + theArea);

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

		// trim the area to sensible bounds
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
			ReferencedEnvelope rEnv = new ReferencedEnvelope(env, _worldMetres);
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

	public double rangeBetween(WorldLocation from, WorldLocation to)
	{
		DirectPosition2D from2D = new DirectPosition2D(from.getLong(),
				from.getLat());
		DirectPosition2D to2D = new DirectPosition2D(to.getLong(), to.getLat());
		GeodeticCalculator calc = new GeodeticCalculator(getCRS2());
		calc.setStartingGeographicPoint(from2D);
		calc.setDestinationGeographicPoint(to2D);
		double range = calc.getOrthodromicDistance();
		return range;
	}

	public double bearingBetween(WorldLocation from, WorldLocation to)
	{
		DirectPosition2D from2D = new DirectPosition2D(from.getLong(),
				from.getLat());
		DirectPosition2D to2D = new DirectPosition2D(to.getLong(), to.getLat());
		GeodeticCalculator calc = new GeodeticCalculator(getCRS2());
		calc.setStartingGeographicPoint(from2D);
		calc.setDestinationGeographicPoint(to2D);
		double bearing = calc.getAzimuth();
		bearing = MWC.Algorithms.Conversions.Degs2Rads(bearing);
		return bearing;
	}

	public WorldLocation add(WorldLocation base, WorldVector delta)
	{
		DirectPosition2D from2D = new DirectPosition2D(base.getLong(),
				base.getLat());
		GeodeticCalculator calc = new GeodeticCalculator(getCRS2());
		calc.setStartingGeographicPoint(from2D);
		double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(delta.getBearing());
		if(brgDegs > 180)
			brgDegs -= 360;
		calc.setDirection(brgDegs,
				delta.getRange());
		Point2D to2D = calc.getDestinationGeographicPoint();
		WorldLocation res = new WorldLocation(to2D.getY(), to2D.getX(),
				base.getDepth() + delta.getDepth());
		return res;
	}

	public WorldVector subtract(WorldLocation from, WorldLocation to)
	{
		return subtract(from, to, null);
	}

	public WorldVector subtract(WorldLocation from, WorldLocation to,
			WorldVector res)
	{
		if (res == null)
			res = new WorldVector(0, 0, 0);
		DirectPosition2D from2D = new DirectPosition2D(from.getLong(),
				from.getLat());
		DirectPosition2D to2D = new DirectPosition2D(to.getLong(), to.getLat());
		GeodeticCalculator calc = new GeodeticCalculator(getCRS2());
		calc.setStartingGeographicPoint(from2D);
		calc.setDestinationGeographicPoint(to2D);
		double range = calc.getOrthodromicDistance();

		range = MWC.Algorithms.Conversions.m2Degs(range);
		double bearing = calc.getAzimuth();
		bearing = MWC.Algorithms.Conversions.Degs2Rads(bearing);
		res.setValues(bearing, range, (to.getDepth() - from.getDepth()));
		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class GtEarthTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public GtEarthTest(String val)
		{
			super(val);
		}

		public void test4326() throws NoSuchAuthorityCodeException,
				FactoryException
		{
			CoordinateReferenceSystem crDegs = CRS.decode("EPSG:4326");
			GeodeticCalculator calc = new GeodeticCalculator(crDegs);
			WorldLocation from = new WorldLocation(0, 0, 0);
			WorldLocation to = new WorldLocation(0, 1, 0);
			DirectPosition2D from2D = new DirectPosition2D(from.getLong(),
					from.getLat());
			DirectPosition2D to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			double range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			double bearing = calc.getAzimuth();
			assertEquals("over a degree", 1, range, 0.01);
			assertEquals("correct bearing", Math.PI / 2,
					MWC.Algorithms.Conversions.Degs2Rads(bearing), 0.0001);

			from = new WorldLocation(60, 0, 0);
			to = new WorldLocation(60, 1, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();

			assertEquals("about 1/2 a degree", 0.5, range, 0.01);
			assertEquals("correct bearing", Math.PI / 2,
					MWC.Algorithms.Conversions.Degs2Rads(bearing), 0.1);

			from = new WorldLocation(0, 0, 0);
			to = new WorldLocation(1, 1, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();

			assertEquals("about 1/2 a degree", 1.411, range, 0.01);
			assertEquals("correct bearing", 45, bearing, 0.2);

			from = new WorldLocation(1, 1, 0);
			to = new WorldLocation(0, 0, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();
			if (bearing < 0)
				bearing += 360;

			assertEquals("about 1/2 a degree", 1.411, range, 0.01);
			assertEquals("correct bearing", 225, bearing, 0.2);

		}

		public void test3395() throws NoSuchAuthorityCodeException,
				FactoryException
		{
			CoordinateReferenceSystem crMetres = CRS.decode("EPSG:3395");
			GeodeticCalculator calc = new GeodeticCalculator(crMetres);
			WorldLocation from = new WorldLocation(0, 0, 0);
			WorldLocation to = new WorldLocation(0, 1, 0);
			DirectPosition2D from2D = new DirectPosition2D(from.getLong(),
					from.getLat());
			DirectPosition2D to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			double range = calc.getOrthodromicDistance();
			double bearing = calc.getAzimuth();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			assertEquals("over a degree", 1, range, 0.01);
			assertEquals("correct bearing", Math.PI / 2,
					MWC.Algorithms.Conversions.Degs2Rads(bearing), 0.0001);

			from = new WorldLocation(60, 0, 0);
			to = new WorldLocation(60, 1, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();

			assertEquals("about 1/2 a degree", 0.5, range, 0.01);
			assertEquals("correct bearing", Math.PI / 2,
					MWC.Algorithms.Conversions.Degs2Rads(bearing), 0.1);

			from = new WorldLocation(0, 0, 0);
			to = new WorldLocation(1, 1, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();

			assertEquals("about 1/2 a degree", 1.411, range, 0.01);
			assertEquals("correct bearing", 45, bearing, 0.2);

			from = new WorldLocation(1, 1, 0);
			to = new WorldLocation(0, 0, 0);
			from2D = new DirectPosition2D(from.getLong(), from.getLat());
			to2D = new DirectPosition2D(to.getLong(), to.getLat());
			calc.setStartingGeographicPoint(from2D);
			calc.setDestinationGeographicPoint(to2D);
			range = calc.getOrthodromicDistance();
			range = MWC.Algorithms.Conversions.m2Degs(range);
			bearing = calc.getAzimuth();
			if (bearing < 0)
				bearing += 360;

			assertEquals("about 1/2 a degree", 1.411, range, 0.01);
			assertEquals("correct bearing", 225, bearing, 0.2);
		}
	}

}

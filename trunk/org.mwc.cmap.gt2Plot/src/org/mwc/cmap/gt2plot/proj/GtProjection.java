package org.mwc.cmap.gt2plot.proj;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.mwc.cmap.gt2plot.GtActivator;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
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

	private final MapContent _map;
	// private AffineTransform worldToScreen;
	// private AffineTransform screenToWorld;
	private final MapViewport _view;

	public GtProjection()
	{
		super("GeoTools");

		_map = new MapContent();
		_view = _map.getViewport();

		// sort out the degs to m transform
		try
		{
			// we'll tell GeoTools to use the projection that's used by most of our charts,
			// so that the chart will be displayed undistorted
			_worldCoords = CRS.decode("EPSG:3395");
			
			// we also need a way to convert a location in degrees to that used by 
			// the charts (metres)
			CoordinateReferenceSystem worldDegs = CRS.decode("EPSG:4326");
			_degs2metres = CRS.findMathTransform(worldDegs, _worldCoords);
		}
		catch (NoSuchAuthorityCodeException e)
		{
			GtActivator.logError(Status.ERROR,
					"Can't find the requested authority whilst trying to create CRS transform", e);
		}
		catch (FactoryException e)
		{
			GtActivator.logError(Status.ERROR,
					"Unexpected problem whilst trying to create CRS transform", e);
		}

		_view.setCoordinateReferenceSystem(_worldCoords);

		_view.setMatchingAspectRatio(true);

		// _map.addMapBoundsListener(new MapBoundsListener()
		// {
		// public void mapBoundsChanged(MapBoundsEvent event)
		// {
		// clearTransforms();
		// }
		// });

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Point toScreen(WorldLocation val)
	{
		Point res = null;

		DirectPosition2D degs = new DirectPosition2D(val.getLong(), val.getLat());
		DirectPosition2D metres = new DirectPosition2D();
		DirectPosition2D screen = new DirectPosition2D();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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

				WorldLocation tl = new WorldLocation(brDegs.y,
						brDegs.x, 0d);
				WorldLocation br = new WorldLocation(tlDegs.y,
						tlDegs.x, 0d);
				WorldArea newArea = new WorldArea(tl, br);
				newArea.normalise();

				setDataArea(newArea);

			}
			catch (MismatchedDimensionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (org.opengis.referencing.operation.NoninvertibleTransformException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (TransformException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void setScreenArea(Dimension theArea)
	{
		super.setScreenArea(theArea);

		java.awt.Rectangle screenArea = new java.awt.Rectangle(0, 0, theArea.width,
				theArea.height);
		_view.setScreenArea(screenArea);
	}

	@Override
	public void setDataArea(WorldArea theArea)
	{
		super.setDataArea(theArea);

		mySetDataArea(theArea);
	}

	private void mySetDataArea(WorldArea theArea)
	{
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
		catch (MismatchedDimensionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public MapContent getMapContent()
	{
		return _map;
	}

	public void addGeoToolsLayer(ExternallyManagedDataLayer gt)
	{
		GeoToolsLayer geoLayer =(GeoToolsLayer) gt;
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

}

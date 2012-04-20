package org.mwc.cmap.gt2plot.proj;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.referencing.CRS;
import org.mwc.cmap.gt2plot.GtActivator;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import MWC.Algorithms.Projections.FlatProjection;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GtProjection extends FlatProjection
{
	private CoordinateReferenceSystem _worldCoords;
	protected MathTransform _degs2metres;

	private MapContent _map;
	private AffineTransform worldToScreen;
	private AffineTransform screenToWorld;

	public GtProjection()
	{
		super.setName("GeoTools");

		_map = new MapContent();

		// sort out the degs to m transform
		try
		{
			_worldCoords = CRS.decode("EPSG:4326");
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

		_map.getViewport().setCoordinateReferenceSystem(_worldCoords);

		_map.addMapBoundsListener(new MapBoundsListener()
		{

			public void mapBoundsChanged(MapBoundsEvent event)
			{
				clearTransforms();
			}
		});

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Point toScreen(WorldLocation val)
	{
		checkTransforms();

		Point res = null;
		if (worldToScreen != null)
		{

			DirectPosition2D degs = new DirectPosition2D(val.getLat(), val.getLong());
			DirectPosition2D screen = new DirectPosition2D();
			try
			{
				// now got to screen
				worldToScreen.transform(degs, screen);

				// output the results
				res = new Point((int) screen.getCoordinate()[0],
						(int) screen.getCoordinate()[1]);
			}
			catch (MismatchedDimensionException e)
			{
				GtActivator.logError(Status.ERROR,
						"Whilst trying to convert to screen coords", e);
			}
		}
		return res;
	}

	@Override
	public WorldLocation toWorld(Point val)
	{
		checkTransforms();

		WorldLocation res = null;
		if (screenToWorld != null)
		{
			DirectPosition2D screen = new DirectPosition2D(val.x, val.y);
			DirectPosition2D degs = new DirectPosition2D();

			try
			{
				// now got to screen
				screenToWorld.transform(screen, degs);
				// screenToWorld.inverseTransform(screen, metres);

				// _degs2metres.inverse().transform(metres, degs);
				res = new WorldLocation(degs.getCoordinate()[0],
						degs.getCoordinate()[1], 0);
			}
			catch (MismatchedDimensionException e)
			{
				GtActivator.logError(Status.ERROR,
						"Whilst trying to set convert to world coords", e);
			}
		}
		return res;
	}

	@Override
	public void zoom(double scaleVal)
	{
		Dimension paneArea = super.getScreenArea();
		WorldArea dataArea = super.getDataArea();
		if (dataArea != null)
		{
			WorldLocation centre = super.getDataArea().getCentre();
			DirectPosition2D mapPos = new DirectPosition2D(centre.getLat(),
					centre.getLong());

			if (worldToScreen != null)
			{
				double scale = worldToScreen.getScaleX();
				double newScale = scale / scaleVal;

				DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d
						* paneArea.width / newScale, mapPos.getY() + 0.5d * paneArea.height
						/ newScale);

				Envelope2D newMapArea = new Envelope2D();
				newMapArea.setFrameFromCenter(mapPos, corner);

				WorldLocation tl = new WorldLocation(newMapArea.getMinX(),
						newMapArea.getMaxY(), 0d);
				WorldLocation br = new WorldLocation(newMapArea.getMaxX(),
						newMapArea.getMinY(), 0d);
				WorldArea newArea = new WorldArea(tl, br);
				setDataArea(newArea);
			}
		}
	}

	@Override
	public void setScreenArea(Dimension theArea)
	{
		clearTransforms();

		java.awt.Rectangle screenArea = new java.awt.Rectangle(0, 0, theArea.width,
				theArea.height);
		_map.getViewport().setScreenArea(screenArea);

		super.setScreenArea(theArea);
	}

	@Override
	public void setDataArea(WorldArea theArea)
	{
		clearTransforms();

		super.setDataArea(theArea);

		WorldLocation tl = theArea.getTopLeft();
		WorldLocation br = theArea.getBottomRight();

		DirectPosition2D tlDegs = new DirectPosition2D(tl.getLat(), tl.getLong());
		DirectPosition2D brDegs = new DirectPosition2D(br.getLat(), br.getLong());

		final CoordinateReferenceSystem mapCoords = _map
				.getCoordinateReferenceSystem();

		// put the coords into an envelope
		Envelope2D env = new Envelope2D(tlDegs, brDegs);

		ReferencedEnvelope rEnv = new ReferencedEnvelope(env, mapCoords);
		_map.getViewport().setBounds(rEnv);

	}

	private void clearTransforms()
	{
		// clear the transforms
		worldToScreen = null;
		screenToWorld = null;
	}

	private void checkTransforms()
	{
		// do we need our transforms?
		if ((worldToScreen == null) || (screenToWorld == null))
		{

			WorldArea dArea = super.getDataArea();
			Dimension sArea = super.getScreenArea();

			if ((dArea != null) && (sArea != null))
			{

				try
				{

					ReferencedEnvelope rEnv = _map.getViewport().getBounds();
					java.awt.Rectangle screenArea = _map.getViewport().getScreenArea();

					double xscale = screenArea.getWidth() / rEnv.getWidth();
					double yscale = screenArea.getHeight() / rEnv.getHeight();

					double scale = Math.min(xscale, yscale);

					double xoff = rEnv.getMedian(0) * scale - screenArea.getCenterX();
					double yoff = rEnv.getMedian(1) * scale + screenArea.getCenterY();

					worldToScreen = new AffineTransform(scale, 0, 0, -scale, -xoff, yoff);

					screenToWorld = worldToScreen.createInverse();

				}
				catch (MismatchedDimensionException e)
				{
					GtActivator.logError(Status.ERROR, "Whilst trying to set transforms",
							e);
				}
				catch (NoninvertibleTransformException e)
				{
					GtActivator.logError(Status.ERROR, "Whilst trying to set transforms",
							e);
				}
			}

		}
	}

	public static class TestProj extends TestCase
	{
		public void testOne() throws NoSuchAuthorityCodeException, FactoryException
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

			// create a point to test
			DirectPosition2D degs = new DirectPosition2D(3, 3);

			// and results object
			DirectPosition2D pixels = new DirectPosition2D();

			// transform the test point
			mc.getViewport().getWorldToScreen().transform(degs, pixels);

			System.out.println("pixels:" + pixels);

		}
	}

}

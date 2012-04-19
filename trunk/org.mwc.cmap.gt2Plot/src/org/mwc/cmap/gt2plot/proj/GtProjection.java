package org.mwc.cmap.gt2plot.proj;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Rectangle;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.geotools.swt.utils.Utils;
import org.mwc.cmap.gt2plot.GtActivator;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

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

		_map.addMapBoundsListener(new MapBoundsListener()
		{

			public void mapBoundsChanged(MapBoundsEvent event)
			{
				initDegs();
			}
		});

	}
	
  /**
   * Calculate the affine transforms used to convert between
   * world and pixel coordinates. The calculations here are very
   * basic and assume a cartesian reference system.
   * <p>
   * Tne transform is calculated such that {@code envelope} will
   * be centred in the display
   *
   * @param envelope the current map extent (world coordinates)
   * @param paintArea the current map pane extent (screen units)
   */
  private void setTransforms(final Envelope envelope, final org.eclipse.swt.graphics.Rectangle paintArea ) {
      ReferencedEnvelope refEnv = null;
      if (envelope != null) {
          refEnv = new ReferencedEnvelope(envelope);
      } else {
          refEnv = worldEnvelope();
          // FIXME content.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
      }

      java.awt.Rectangle awtPaintArea = Utils.toAwtRectangle(paintArea);
      double xscale = awtPaintArea.getWidth() / refEnv.getWidth();
      double yscale = awtPaintArea.getHeight() / refEnv.getHeight();

      double scale = Math.min(xscale, yscale);

      double xoff = refEnv.getMedian(0) * scale - awtPaintArea.getCenterX();
      double yoff = refEnv.getMedian(1) * scale + awtPaintArea.getCenterY();

      worldToScreen = new AffineTransform(scale, 0, 0, -scale, -xoff, yoff);
      try {
          screenToWorld = worldToScreen.createInverse();

      } catch (NoninvertibleTransformException ex) {
          ex.printStackTrace();
      }
  }
  
  private ReferencedEnvelope worldEnvelope() {
    return new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84);
}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Point toScreen(WorldLocation val)
	{
		Point res = null;
		if (worldToScreen != null)
		{

			DirectPosition2D degs = new DirectPosition2D(val.getLat(), val.getLong());
			DirectPosition2D metres = new DirectPosition2D();
			DirectPosition2D screen = new DirectPosition2D();
			try
			{

				// get to meters first
				_degs2metres.transform(degs, metres);

				// now got to screen
				worldToScreen.transform(metres, screen);

				// output the results
				res = new Point((int) screen.getCoordinate()[0],
						(int) screen.getCoordinate()[1]);
			}
			catch (MismatchedDimensionException e)
			{
				e.printStackTrace();
			}
			catch (TransformException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}

	@Override
	public WorldLocation toWorld(Point val)
	{
		WorldLocation res = null;
		if (screenToWorld != null)
		{
			DirectPosition2D screen = new DirectPosition2D(val.x, val.y);
			DirectPosition2D metres = new DirectPosition2D();
			DirectPosition2D degs = new DirectPosition2D();
			try
			{
				// now got to screen
				screenToWorld.inverseTransform(screen, metres);

				_degs2metres.inverse().transform(metres, degs);
				res = new WorldLocation(degs.getCoordinate()[0],
						degs.getCoordinate()[1], 0);
			}
			catch (MismatchedDimensionException e)
			{
				e.printStackTrace();
			}
			catch (NoninvertibleTransformException e)
			{
				e.printStackTrace();
			}
			catch (org.opengis.referencing.operation.NoninvertibleTransformException e)
			{
				e.printStackTrace();
			}
			catch (TransformException e)
			{
				e.printStackTrace();
			}
		}
		return res;
	}

	@Override
	public void setDataArea(WorldArea theArea)
	{
		initDegs();

		WorldLocation tl = theArea.getTopLeft();
		WorldLocation br = theArea.getBottomRight();

		DirectPosition2D tlDegs = new DirectPosition2D(tl.getLat(), tl.getLong());
		DirectPosition2D brDegs = new DirectPosition2D(br.getLat(), br.getLong());

		DirectPosition2D tlMetres = new DirectPosition2D();
		DirectPosition2D brMetres = new DirectPosition2D();

		try
		{
			final CoordinateReferenceSystem mapCoords = _map
					.getCoordinateReferenceSystem();

			// convert to metres
			_degs2metres.transform(tlDegs, tlMetres);
			_degs2metres.transform(brDegs, brMetres);

			// put the coords into an envelope
			Envelope2D env = new Envelope2D(tlMetres, brMetres);

			// when the app first loads, the 'setDisplayArea' doesn't work
			// because it doesn't
			// have a screen area. So, we give the MapContent a default area.
			// Note:
			// we have to do this before we do 'setDisplayArea' because once the
			// map is up
			// & running, calling this method last may result in the chart being
			// requested to
			// plot an area that's a different shape to the actual window -
			// which shows
			// a distorted image.
			ReferencedEnvelope rEnv = new ReferencedEnvelope(env, mapCoords);
			_map.getViewport().setBounds(rEnv);

			// setup the transformations
			Dimension sArea = super.getScreenArea();
			Rectangle screenArea = new Rectangle(0, 0, sArea.width, sArea.height) ;
			setTransforms(env, screenArea );
			
			super.setDataArea(theArea);
		}
		catch (ProjectionException e)
		{
			GtActivator.logError(Status.ERROR, "Whilst trying to set data area", e);
		}
		catch (MismatchedDimensionException e)
		{
			GtActivator.logError(Status.ERROR, "Whilst trying to set data area", e);
		}
		catch (TransformException e)
		{
			GtActivator.logError(Status.ERROR, "Whilst trying to set data area", e);
		}
	}

	private void initDegs()
	{
		try
		{
			_degs2metres = CRS.findMathTransform(_worldCoords, _map.getCoordinateReferenceSystem(), true);
		}
		catch (FactoryException e)
		{
			e.printStackTrace();
		}
	}

}

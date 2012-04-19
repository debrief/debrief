package org.mwc.cmap.gt2plot.proj;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.eclipse.core.runtime.Status;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.mwc.cmap.gt2plot.GtActivator;
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

	private IDebriefMapPane _mapPane;
	private CoordinateReferenceSystem _worldCoords;
	protected MathTransform _degs2metres;

	public GtProjection(IDebriefMapPane mapPane)
	{
		super.setName("GeoTools");

		_mapPane = mapPane;

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

		_mapPane.getMapContent().addMapBoundsListener(new MapBoundsListener()
		{

			public void mapBoundsChanged(MapBoundsEvent event)
			{
				initDegs();
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
		AffineTransform worldToScreenTransform = _mapPane
				.getWorldToScreenTransform();
		Point res = null;
		if (worldToScreenTransform != null)
		{

			DirectPosition2D degs = new DirectPosition2D(val.getLat(), val.getLong());
			DirectPosition2D metres = new DirectPosition2D();
			DirectPosition2D screen = new DirectPosition2D();
			try
			{

				// get to meters first
				_degs2metres.transform(degs, metres);

				// now got to screen
				worldToScreenTransform.transform(metres, screen);

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
		AffineTransform worldToScreenTransform = _mapPane
				.getWorldToScreenTransform();
		WorldLocation res = null;
		if (worldToScreenTransform != null)
		{
			DirectPosition2D screen = new DirectPosition2D(val.x, val.y);
			DirectPosition2D metres = new DirectPosition2D();
			DirectPosition2D degs = new DirectPosition2D();
			try
			{
				// now got to screen
				worldToScreenTransform.inverseTransform(screen, metres);

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
				// TODO Auto-generated catch block
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
			final CoordinateReferenceSystem mapCoords = _mapPane.getMapContent()
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
			_mapPane.getMapContent().getViewport().setBounds(rEnv);

			// tell the SWT Pane about the area we want to show. Note: we can
			// pass an odd-shaped
			// rectangle to this method: the pane will set the correct
			// proportions itself.
			_mapPane.setDisplayArea(env);

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
			_degs2metres = CRS.findMathTransform(_worldCoords, _mapPane
					.getMapContent().getCoordinateReferenceSystem(), true);
		}
		catch (FactoryException e)
		{
			e.printStackTrace();
		}
	}

}

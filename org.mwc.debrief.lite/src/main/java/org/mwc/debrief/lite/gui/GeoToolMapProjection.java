package org.mwc.debrief.lite.gui;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GeoToolMapProjection extends PlainProjection
{
  private static final String WORLD_PROJECTION = "EPSG:3395"; // 3395 for Mercator proj
  private static final String DATA_PROJECTION = "EPSG:4326";
  /**
   * 
   */
  private static final long serialVersionUID = 3398817999418475368L;
  private final MapContent _map;
  private CoordinateReferenceSystem _worldCoords;
  private MathTransform _degs2metres;
  private final MapViewport _view;
  private DirectPosition2D _workDegs;
  private DirectPosition2D _workMetres;
  private DirectPosition2D _workScreen;
  private final Layers _layers;

  @Override
  public WorldArea getVisibleDataArea()
  {
    return _layers.getBounds();
  }

  public GeoToolMapProjection(final MapContent map, final Layers data)
  {
    super("GeoTools Map");
    _map = map;
    _view = _map.getViewport();
    _layers = data;

    // initialise our working data stores
    _workDegs = new DirectPosition2D();
    _workMetres = new DirectPosition2D();
    _workScreen = new DirectPosition2D();
    // we'll tell GeoTools to use the projection that's used by most of our
    // charts,
    // so that the chart will be displayed undistorted
    try
    {
      _worldCoords = CRS.decode(WORLD_PROJECTION);
      // we also need a way to convert a location in degrees to that used by
      // the charts (metres)
      final CoordinateReferenceSystem worldDegs = CRS.decode(DATA_PROJECTION);
      _degs2metres = CRS.findMathTransform(worldDegs, _worldCoords);
    }
    catch (FactoryException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public Point toScreen(WorldLocation val)
  {
    Point res = null;
    // and now for the actual projection bit
    _workDegs.setLocation(val.getLong(), val.getLat());
    try
    {
      _degs2metres.transform(_workDegs, _workMetres);
      // now got to screen
      // _view.getWorldToScreen().transform(_workMetres, _workScreen);
      _view.getWorldToScreen().transform(_workDegs, _workScreen);
      // output the results
      res = new Point((int) _workScreen.getCoordinate()[0], (int) _workScreen
          .getCoordinate()[1]);
    }
    catch (MismatchedDimensionException | TransformException e)
    {
      e.printStackTrace();
    }
    return res;
  }
  

  @Override
  public WorldArea getDataArea()
  {
    return _layers.getBounds();
  }

  @Override
  public WorldLocation toWorld(Point val)
  {
    WorldLocation res = null;
    _workScreen.setLocation(val.x, val.y);
    try
    {
      // hmm, do we have an area?
      final WorldArea dArea = this.getDataArea();
      if (dArea.getWidth() > 0 || dArea.getHeight() > 0)
      {
        // now got to screen
        final AffineTransform currentTransform = _view.getScreenToWorld();
        if (currentTransform != null)
        {
          currentTransform.transform(_workScreen, _workMetres);
          _degs2metres.inverse().transform(_workMetres, _workDegs);
        }
        res = new WorldLocation(_workDegs.getCoordinate()[1], _workDegs
            .getCoordinate()[0], 0);
      }
    }
    catch (final MismatchedDimensionException e)
    {
      Application.logError2(Application.ERROR,
          "Whilst trying to set convert to world coords", e);
    }
    catch (final org.opengis.referencing.operation.NoninvertibleTransformException e)
    {
      Application.logError2(Application.ERROR,
          "Unexpected non-invertable problem whilst performing screen to world",
          e);
    }
    catch (final TransformException e)
    {
      Application.logError2(Application.ERROR,
          "Unexpected transform problem whilst performing screen to world", e);
    }
    return res;
  }

  @Override
  public void zoom(double value)
  {
  }
}
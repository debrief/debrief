package org.mwc.debrief.lite.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.geotools.factory.Hints;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.mwc.cmap.geotools.gt2plot.GeoToolsLayer;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GeoToolMapProjection extends PlainProjection implements
    GeoToolsHandler
{
  private static final String WORLD_PROJECTION = "EPSG:3395"; // 3395 for Mercator proj (? or may be
                                                              // 3857?)
  private static final String DATA_PROJECTION = "EPSG:4326";
  private static final long serialVersionUID = 3398817999418475368L;
  /**
   *
   */
  private final CoordinateReferenceSystem dataCRS;
  private final MapViewport _view;
  private final Layers _layers;
  private final MapContent _map;
  private final MathTransform data_transform;

  public GeoToolMapProjection(final MapContent map, final Layers data)
  {
    super("GeoTools Map");
    _map = map;
    _view = map.getViewport();
    _layers = data;

    // initialise our working data stores

    // we'll tell GeoTools to use the projection that's used by most of our
    // charts,
    // so that the chart will be displayed undistorted

    // note - we want to store the var fields as final values,
    // but since they're created inside a try block,
    // we'll put them into temporary vars first
    MathTransform data_transform_val = null;
    CoordinateReferenceSystem dataCRS_val = null;
    try
    {
      final CoordinateReferenceSystem worldCoords = CRS.decode(
          WORLD_PROJECTION);
      // we also need a way to convert a location in degrees to that used by
      // the charts (metres)
      Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,
          Boolean.TRUE);
      final CoordinateReferenceSystem worldDegs = CRS.decode(DATA_PROJECTION);
      dataCRS_val = worldDegs;
      data_transform_val = CRS.findMathTransform(worldDegs, worldCoords);

      // put the map into Mercator Proj
      _view.setCoordinateReferenceSystem(worldCoords);
      // limit bounds
      ReferencedEnvelope bounds = new ReferencedEnvelope(-180, 180,
          -85.05112878, 85.05112878, worldDegs);
      bounds = bounds.transform(worldCoords, true);
      _view.setBounds(bounds);
    }
    catch (final FactoryException | TransformException e)
    {
      Application.logError2(ToolParent.ERROR, "Failure in projection transform",
          e);
    }
    dataCRS = dataCRS_val;
    data_transform = data_transform_val;
  }

  @Override
  public void addGeoToolsLayer(final ExternallyManagedDataLayer layer)
  {
    final GeoToolsLayer geoLayer = (GeoToolsLayer) layer;
    geoLayer.setMap(_map);
  }

  @Override
  public void dispose()
  {
    if (_map != null)
    {
      _map.dispose();
    }
  }

  @Override
  public WorldArea getDataArea()
  {
    return _layers.getBounds();
  }

  public MathTransform getDataTransform()
  {
    return data_transform;
  }

  @Override
  public Dimension getScreenArea()
  {
    final Rectangle rect = _view.getScreenArea();
    return new Dimension((int) rect.getWidth(), (int) rect.getHeight());
  }

  @Override
  public WorldArea getVisibleDataArea()
  {
    return _layers.getBounds();
  }

  @Override
  public Point toScreen(final WorldLocation val)
  {
    final DirectPosition2D workDegs = new DirectPosition2D();
    final DirectPosition2D workScreen = new DirectPosition2D();

    Point res = null;
    // and now for the actual projection bit
    workDegs.setLocation(val.getLong(), val.getLat());
    if (_view.getCoordinateReferenceSystem() != dataCRS)
    {
      try
      {
        data_transform.transform(workDegs, workDegs);
        _view.getWorldToScreen().transform(workDegs, workScreen);
        // output the results
        res = new Point((int) workScreen.getCoordinate()[0], (int) workScreen
            .getCoordinate()[1]);
      }
      catch (MismatchedDimensionException | TransformException e)
      {
        Application.logError2(ToolParent.ERROR,
            "Failure in projection transform in toScreen operation:" + e
                .getMessage() + " from location:" + val, null);
      }
    }
    return res;
  }

  @Override
  public WorldLocation toWorld(final Point val)
  {
    final DirectPosition2D workDegs = new DirectPosition2D();
    final DirectPosition2D workScreen = new DirectPosition2D();

    WorldLocation res = null;
    workScreen.setLocation(val.x, val.y);
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
          currentTransform.transform(workScreen, workDegs);
        }

        if (_view.getCoordinateReferenceSystem() != dataCRS)
        {
          try
          {
            data_transform.inverse().transform(workDegs, workDegs);
          }
          catch (MismatchedDimensionException | TransformException e)
          {
            Application.logError2(ToolParent.ERROR,
                "Failure in projection transform", e);
          }
        }

        res = new WorldLocation(workDegs.getCoordinate()[1], workDegs
            .getCoordinate()[0], 0);
      }
    }
    catch (final MismatchedDimensionException e)
    {
      Application.logError2(ToolParent.ERROR,
          "Whilst trying to set convert to world coords", e);
    }
    return res;
  }

  @Override
  public void zoom(final double value)
  {
  }
}
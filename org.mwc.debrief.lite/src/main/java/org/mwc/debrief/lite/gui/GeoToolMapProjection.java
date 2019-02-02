package org.mwc.debrief.lite.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.projection.ProjectionException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
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
  private MathTransform _degs2metres;
  private final MapViewport _view;
  private final DirectPosition2D _workDegs;
  private final DirectPosition2D _workMetres;
  private final DirectPosition2D _workScreen;
  private final Layers _layers;
  private CoordinateReferenceSystem _worldCoords;

  public GeoToolMapProjection(final MapContent map, final Layers data)
  {
    super("GeoTools Map");
    _view = map.getViewport();
    _layers = data;

    // initialise our working data stores
    _workDegs = new DirectPosition2D();
    _workMetres = new DirectPosition2D();
    _workScreen = new DirectPosition2D();
    
    // set the aspect radio matching to true. The default
    // value for this was false - but when we did fit to
    // window, it wasn't putting the specified area in the centre of the shape
    _view.setMatchingAspectRatio(true);

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
      _view.setCoordinateReferenceSystem(_worldCoords);
    }
    catch (final FactoryException e)
    {
      e.printStackTrace();
    }
    
    // SPECIAL HANDLING: this is the kludge to ensure the aspect ratio is kept
    // constant
    _view.setMatchingAspectRatio(true);
  }

  @Override
  public WorldArea getDataArea()
  {
    return _layers.getBounds();
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
    Point res = null;
    // and now for the actual projection bit
    _workDegs.setLocation(val.getLat(), val.getLong());
    try
    {
      _degs2metres.transform(_workDegs, _workMetres);
      // now got to screen
      // _view.getWorldToScreen().transform(_workMetres, _workScreen);
      _view.getWorldToScreen().transform(_workMetres, _workScreen);
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
  public WorldLocation toWorld(final Point val)
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
        res = new WorldLocation(_workDegs.getCoordinate()[0], _workDegs
            .getCoordinate()[1], 0);
      }
    }
    catch (final MismatchedDimensionException e)
    {
      Application.logError2(ToolParent.ERROR,
          "Whilst trying to set convert to world coords", e);
    }
    catch (final org.opengis.referencing.operation.NoninvertibleTransformException e)
    {
      Application.logError2(ToolParent.ERROR,
          "Unexpected non-invertable problem whilst performing screen to world",
          e);
    }
    catch (final TransformException e)
    {
      Application.logError2(ToolParent.ERROR,
          "Unexpected transform problem whilst performing screen to world", e);
    }
    return res;
  }

  @Override
  public void zoom(final double value)
  {
  }
  

  @Override
  public void setDataArea(final WorldArea theArea)
  {
    if (theArea == null)
    {
      logError(ToolParent.WARNING, "GtProjection received null in setDataArea - maintainer to be informed", null);
      return;
    }
    // trim the area to sensible bounds
    theArea.trim();

    mySetDataArea(theArea);

    // and store it in the parent;
    super.setDataArea(theArea);
  }
  

  private void logError(int warning, String string, Object object)
  {
    System.err.println(string);
  }

  private void mySetDataArea(final WorldArea theArea)
  {
    // double-check we're not already ste to this
    if (theArea.equals(super.getDataArea()))
    {
      // System.err.println("OVER-RIDING EXISTING AREA - TRAP THIS INSTANCE");
    //  return;
    }

    // trim the coordinates
    gtTrim(theArea);

    final WorldLocation tl = theArea.getTopLeft();
    final WorldLocation br = theArea.getBottomRight();

    final DirectPosition2D tlDegs = new DirectPosition2D(tl.getLat(),
        tl.getLong());
    final DirectPosition2D brDegs = new DirectPosition2D(br.getLat(),
        br.getLong());

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
      logError(ToolParent.ERROR,
          "trouble with proj, probably zoomed out too far", e);
    }
    catch (final MismatchedDimensionException e)
    {
      logError(ToolParent.ERROR, "unknown trouble with proj", e);
    }
    catch (final TransformException e)
    {
      logError(ToolParent.ERROR, "unknown trouble with proj", e);
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
}
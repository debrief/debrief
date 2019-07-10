/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.map;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.Hints;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.geotools.swing.tool.CursorTool;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

/**
 *
 * @author Unni Mana <unnivm@gmail.com>
 *
 */
public class GeoToolMapRenderer
{

  private static class CustomMapPane extends JMapPane
  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String WORLD_PROJECTION = "EPSG:3395"; // 3395 for Mercator proj (? or may
                                                                // be 3857?)
    private static final String DATA_PROJECTION = "EPSG:4326";
    private final MouseDragLine dragLine;

    private final GeoToolMapRenderer _renderer;
    private final MapMouseListener mouseMotionListener = new MapMouseAdapter()
    {

      void handleMouseMovement(final MapMouseEvent ev)
      {
        // mouse pos in Map coordinates
        final DirectPosition2D curPos = ev.getWorldPos();

        if (ev.getWorldPos()
            .getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84)
        {
          try
          {
            data_transform.transform(curPos, curPos);
          }
          catch (MismatchedDimensionException | TransformException e)
          {
            Application.logError2(ToolParent.ERROR,
                "Failure in projection transform", e);
          }
        }

        final WorldLocation current = new WorldLocation(curPos.getY(), curPos
            .getX(), 0);
        final String message = BriefFormatLocation.toString(current);
        DebriefLiteApp.updateStatusMessage(message);
      }

      @Override
      public void onMouseDragged(final MapMouseEvent arg0)
      {
        if (!(currentCursorTool instanceof RangeBearingTool))
        {
          handleMouseMovement(arg0);
        }
      }

      @Override
      public void onMouseEntered(final MapMouseEvent arg0)
      {
        handleMouseMovement(arg0);
      }

      @Override
      public void onMouseExited(final MapMouseEvent arg0)
      {
        handleMouseMovement(arg0);
      }

      @Override
      public void onMouseMoved(final MapMouseEvent arg0)
      {
        handleMouseMovement(arg0);
      }

      @Override
      public void onMouseWheelMoved(final MapMouseEvent arg0)
      {
        handleMouseMovement(arg0);
      }
    };

    private CoordinateReferenceSystem worldCoords;

    private CoordinateReferenceSystem worldDegs;

    private MathTransform data_transform;

    public CustomMapPane(final GeoToolMapRenderer geoToolMapRenderer, Color backColor)
    {
      super();
      
      // Would be better to pass in a GeoToolMapProjection or GTProjection here?
      try
      {
        worldCoords = CRS.decode(WORLD_PROJECTION);

        Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,
            Boolean.TRUE);
        worldDegs = CRS.decode(DATA_PROJECTION);
        data_transform = CRS.findMathTransform(worldCoords, worldDegs);
      }
      catch (final FactoryException e)
      {
        Application.logError2(ToolParent.ERROR,
            "Failure in projection transform", e);
      }
      _renderer = geoToolMapRenderer;

      dragLine = new MouseDragLine(this);
      addMouseListener(dragLine);
      addMouseMotionListener(dragLine);
      addMouseListener(mouseMotionListener);
      
      // try to set background color
      super.setBackground(backColor);
    }

    @Override
    protected void paintComponent(final Graphics arg0)
    {
      super.paintComponent(arg0);
      _renderer.paintEvent(arg0);
    }

    @Override
    public void setCursorTool(final CursorTool tool)
    {
      paramsLock.writeLock().lock();
      try
      {
        if (currentCursorTool != null)
        {
          mouseEventDispatcher.removeMouseListener(currentCursorTool);
        }

        currentCursorTool = tool;

        if (currentCursorTool == null)
        {
          setCursor(Cursor.getDefaultCursor());
          dragBox.setEnabled(false);
          dragLine.setEnabled(false);
        }
        else
        {
          setCursor(currentCursorTool.getCursor());
          dragLine.setEnabled(currentCursorTool instanceof RangeBearingTool);
          dragBox.setEnabled(currentCursorTool.drawDragBox());
          currentCursorTool.setMapPane(this);
          mouseEventDispatcher.addMouseListener(currentCursorTool);
        }

      }
      finally
      {
        paramsLock.writeLock().unlock();
      }
    }
  }

  public static interface MapRenderer
  {
    public void paint(final Graphics gc);
  }

  private CustomMapPane mapPane;

  private final MapContent mapContent;

  private Graphics graphics;

  private SimpleFeatureSource featureSource;

  private final List<MapRenderer> _myRenderers = new ArrayList<MapRenderer>();

  public GeoToolMapRenderer()
  {
    super();

    // Create a map content and add our shape file to it
    mapContent = new MapContent();
    mapContent.setTitle("Debrief Lite");
  }

  public void addRenderer(final MapRenderer renderer)
  {
    _myRenderers.add(renderer);
  }

  public void createMapLayout(final Color backColor)
  {
    mapPane = new CustomMapPane(this, backColor);
    final StreamingRenderer streamer = new StreamingRenderer();
    mapPane.setRenderer(streamer);
    mapPane.setMapContent(mapContent);
  }

  /**
   * returns java.awt.Graphics object
   *
   * @return
   */
  public Graphics getGraphicsContext()
  {
    return graphics;
  }

  public Component getMap()
  {
    return mapPane;
  }

  /**
   * return map component
   *
   * @return
   */
  public MapContent getMapComponent()
  {
    return mapContent;
  }

  public MathTransform getTransform()
  {
    return mapPane.data_transform;
  }

  /**
   * gets a MathTransform object
   *
   * @return MathTransform
   */
  public MathTransform getTransformObject()
  {
    final SimpleFeatureType schema = featureSource.getSchema();
    final CoordinateReferenceSystem dataCRS = schema
        .getCoordinateReferenceSystem();
    final CoordinateReferenceSystem worldCRS = mapContent
        .getCoordinateReferenceSystem();
    MathTransform transform = null;
    try
    {
      transform = CRS.findMathTransform(dataCRS, worldCRS);
    }
    catch (final FactoryException e)
    {
      Application.logError2(ToolParent.ERROR, "Failure in projection transform",
          e);
    }
    return transform;
  }

  private void paintEvent(final Graphics arg0)
  {
    for (final MapRenderer r : _myRenderers)
    {
      r.paint(arg0);
    }
  }

}

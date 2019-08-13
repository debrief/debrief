package org.mwc.debrief.lite.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.RenderedImage;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.geotools.util.factory.Hints;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

public class LiteMapPane extends JMapPane
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String WORLD_PROJECTION = "EPSG:3395"; // 3395 for Mercator proj (? or may

  // be 3857?)
  private static final String DATA_PROJECTION = "EPSG:4326";
  /**
   * background transparency
   *
   */
  private float mapTransparency;
  private final MouseDragLine dragLine;

  private final GeoToolMapRenderer _renderer;

  private final MathTransform data_transform;

  public LiteMapPane(final GeoToolMapRenderer geoToolMapRenderer, final float alpha)
  {
    super();
    
    mapTransparency = alpha;

    // Would be better to pass in a GeoToolMapProjection or GTProjection here?
    MathTransform theTransform = null;
    try
    {
      final CoordinateReferenceSystem worldCoords = CRS.decode(
          WORLD_PROJECTION);

      Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,
          Boolean.TRUE);
      final CoordinateReferenceSystem worldDegs = CRS.decode(DATA_PROJECTION);
      theTransform = CRS.findMathTransform(worldCoords, worldDegs);
    }
    catch (final FactoryException e)
    {
      Application.logError2(ToolParent.ERROR, "Failure in projection transform",
          e);
    }

    data_transform = theTransform;
    _renderer = geoToolMapRenderer;
    dragLine = new MouseDragLine(this);

    addMouseListener(dragLine);
    addMouseMotionListener(dragLine);
    addMouseListener(getMouseListener(data_transform));

    // try to set background color
    super.setBackground(new Color(135, 172, 215));
  }
  
  public MathTransform getTransform()
  {
    return data_transform;
  }

  public MapMouseAdapter getMouseListener(final MathTransform transform)
  {
    return new MapMouseAdapter()
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
            transform.transform(curPos, curPos);
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

  }

  @Override
  protected void paintComponent(final Graphics g)
  {
    // don't ask the parent to paint, since we're doing it, instead
    //super.paintComponent(g);

    // draw in background
    Dimension dim = this.getSize();
    g.setColor(Color.white);
    g.fillRect(0, 0, dim.width, dim.height);
    
    if (drawingLock.tryLock())
    {
      try
      {
        final RenderedImage image = getBaseImage();
        if (image != null)
        {
          final Graphics2D g2 = (Graphics2D) g;

          // remember the imaging composite
          final Composite before = g2.getComposite();

          // ok, set transparency
          final AlphaComposite transOne = AlphaComposite.getInstance(
              AlphaComposite.SRC_OVER, mapTransparency);
          g2.setComposite(transOne);

          // draw the image
          g2.drawImage((Image) image, imageOrigin.x, imageOrigin.y, null);

          // restore the mode
          g2.setComposite(before);

        }
      }
      finally
      {
        drawingLock.unlock();
      }
    }
    _renderer.paintEvent(g);
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

  // @Override
  // protected void paintComponent(final Graphics arg0)
  // {
  // super.paintComponent(arg0);
  // }

  public void setTransparency(final float transparency)
  {
    mapTransparency = transparency;
  }
}
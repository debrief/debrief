package org.mwc.debrief.lite.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MouseDragBox;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
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

  private final GeoToolMapRenderer _renderer;

  private final MathTransform data_transform;

  private final ArrayList<ActionListener> repaintListeners = new ArrayList<>();

  public LiteMapPane(final GeoToolMapRenderer geoToolMapRenderer,
      final float alpha)
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

    addMouseListener(getMouseListener(data_transform));

    disableBoxDrawRightClick();

    // try to set background color
    super.setBackground(new Color(135, 172, 215));
  }

  public void addRepaintListener(final ActionListener actionListener)
  {
    repaintListeners.add(actionListener);
  }

  private void disableBoxDrawRightClick()
  {
    final MouseListener[] listeners = getMouseListeners();
    MouseDragBox dragbox = null;
    for (final MouseListener l : listeners)
    {
      if (l instanceof MouseDragBox)
      {
        dragbox = ((MouseDragBox) l);
      }
    }
    final MouseDragBox finalDragbox = dragbox;

    addMouseListener(new MapMouseListener()
    {

      @Override
      public void onMouseClicked(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMouseDragged(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMouseEntered(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMouseExited(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMouseMoved(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMousePressed(final MapMouseEvent paramMapMouseEvent)
      {
        if (finalDragbox != null)
        {
          final boolean isRightClick = paramMapMouseEvent
              .getButton() == MouseEvent.BUTTON3;
          finalDragbox.setEnabled(!isRightClick && currentCursorTool != null
              && currentCursorTool.drawDragBox());
        }
      }

      @Override
      public void onMouseReleased(final MapMouseEvent paramMapMouseEvent)
      {

      }

      @Override
      public void onMouseWheelMoved(final MapMouseEvent paramMapMouseEvent)
      {

      }
    });
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

  public MathTransform getTransform()
  {
    return data_transform;
  }

  /**
   * There are some classes (for example MouseDragLine), which need to know when the map has been
   * repainted. Simply add an ActionEvent to the repaintListeners list and it will be notified :)
   *
   * Don't forget to call the notifier... Saul Hidalgo
   */
  private void notifyRepaintListeners()
  {
    if (repaintListeners != null)
    {
      for (final ActionListener action : repaintListeners)
      {
        action.actionPerformed(null);
      }
    }
  }

  // @Override
  // protected void paintComponent(final Graphics arg0)
  // {
  // super.paintComponent(arg0);
  // }

  @Override
  public void paint(final Graphics g)
  {
    super.paint(g);
    notifyRepaintListeners();
  }

  @Override
  protected void paintComponent(final Graphics g)
  {
    // don't ask the parent to paint, since we're doing it, instead
    // super.paintComponent(g);

    // draw in background
    final Dimension dim = this.getSize();
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

  public void setContextMenu(final ContextMenu menu)
  {
    setComponentPopupMenu(menu);
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
        if (currentCursorTool instanceof RangeBearingTool)
        {
          ((RangeBearingTool) currentCursorTool).eraseOldDrawing();
        }
      }

      currentCursorTool = tool;

      if (currentCursorTool == null)
      {
        setCursor(Cursor.getDefaultCursor());
        dragBox.setEnabled(false);
      }
      else
      {
        setCursor(currentCursorTool.getCursor());
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

  public void setTransparency(final float transparency)
  {
    mapTransparency = transparency;
  }

}
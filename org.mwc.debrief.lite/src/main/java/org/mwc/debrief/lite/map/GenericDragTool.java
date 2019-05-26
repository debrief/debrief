package org.mwc.debrief.lite.map;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

public class GenericDragTool extends CursorTool
{

  /** Tool name */
  public static final String TOOL_NAME = "Drag Element";

  /** Tool tip text */
  public static final String TOOL_TIP = "Drag Element";

  /** Cursor */
  public static final String CURSOR_IMAGE =
      "/org/geotools/swing/icons/mActionIdentify.png";

  /** Icon for the control */
  public static final String ICON_IMAGE =
      "/org/geotools/swing/icons/mActionPan.png";

  /** Cursor hotspot coordinates */
  public static final Point CURSOR_HOTSPOT = new Point(15, 15);

  protected final Cursor cursor;

  protected boolean panning;

  protected Point panePos;

  final Layers layers;

  final GeoToolMapProjection _projection;

  protected final JMapPane _mapPane;

  /**
   * the component we're going to drag
   */
  protected WorldLocation _hoverComponent;

  /**
   * the layer to update when dragging is complete
   */
  protected Layer _parentLayer;

  /**
   * how far the mouse has to be dragged before it's registered as a drag operation
   */
  protected final double JITTER = 0.02;

  public GenericDragTool(final Layers _layers,
      final GeoToolMapProjection projection, final JMapPane mapPane)
  {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final ImageIcon imgIcon = new ImageIcon(getClass().getResource(
        CURSOR_IMAGE));
    cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);
    this.layers = _layers;
    this._projection = projection;
    this._mapPane = mapPane;
  }

  /**
   * Returns false to indicate that this tool does not draw a box on the map display when the mouse
   * is being dragged
   */
  @Override
  public boolean drawDragBox()
  {
    return false;
  }

  /** Get the mouse cursor for this tool */
  @Override
  public Cursor getCursor()
  {
    return cursor;
  }

  /**
   * Move the point a bit to the upper left corner to adjust the animation with the mouse icon.
   *
   * @param originalPoint
   * @return
   */
  protected Point mouseDelta(final Point originalPoint)
  {
    return new Point(originalPoint.x - 10, originalPoint.y - 10);
  }

  /**
   * If this button release is the end of a mouse dragged event, requests the map mapPane to repaint
   * the display
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseReleased(final MapMouseEvent ev)
  {
    panning = false;
  }
}

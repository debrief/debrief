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
      "/icons/16/whitehand.png";

  /** Icon for the control */
  public static final String ICON_IMAGE =
      "/icons/16/whitehand.png";

  /** Icon for the control */
  public static final String ICON_IMAGE_GREEN =
      "/icons/16/SelectFeatureHit.png";

  /** Icon for the control */
  public static final String ICON_IMAGE_DRAGGING =
      "/icons/16/SelectFeatureHitDown.png";

  /** Cursor hotspot coordinates */
  public static final Point CURSOR_HOTSPOT = new Point(15, 15);

  protected final Cursor normalCursor;
  
  protected final Cursor greenCursor;
  
  protected final Cursor draggingCursor;
  
  /**
   * We are going to use this to avoid re-assigning the same cursor.
   */
  protected Cursor lastCursor;

  protected boolean panning;

  protected Point panePos;

  protected final Layers layers;

  protected final GeoToolMapProjection _projection;

  protected final JMapPane _mapPane;

  /** how close we have to be (in screen pixels) to display
   * hotspot cursor 
   */
  protected static double SCREEN_JITTER = 11;
  
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
  protected final double JITTER = SCREEN_JITTER;

  public GenericDragTool(final Layers _layers,
      final GeoToolMapProjection projection, final JMapPane mapPane)
  {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final ImageIcon imgIcon = new ImageIcon(getClass().getResource(
        CURSOR_IMAGE));
    normalCursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);

    final ImageIcon imgGreenIcon = new ImageIcon(getClass().getResource(
        ICON_IMAGE_GREEN));
    greenCursor = tk.createCustomCursor(imgGreenIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);

    final ImageIcon imgDragIcon = new ImageIcon(getClass().getResource(
        ICON_IMAGE_DRAGGING));
    draggingCursor = tk.createCustomCursor(imgDragIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);
    
    lastCursor = normalCursor;
    
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
    return normalCursor;
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

    lastCursor = greenCursor;
    _mapPane.setCursor(greenCursor);
  }

  @Override
  public void onMousePressed(MapMouseEvent ev)
  {
    if ( lastCursor.equals(greenCursor) )
    {
      lastCursor = draggingCursor;
      _mapPane.setCursor(draggingCursor);
    }
  }
}

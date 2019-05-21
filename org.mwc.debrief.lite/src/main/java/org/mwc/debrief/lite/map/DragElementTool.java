package org.mwc.debrief.lite.map;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

public class DragElementTool extends CursorTool
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
  
  private final Cursor cursor;
  
  boolean panning;
  
  public DragElementTool()
  {
    Toolkit tk = Toolkit.getDefaultToolkit();
    ImageIcon imgIcon = new ImageIcon(getClass().getResource(CURSOR_IMAGE));
    cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT, TOOL_NAME);
  }
  
  /**
   * Respond to a mouse button press event from the map mapPane. This may signal the start of a
   * mouse drag. Records the event's window position.
   *
   * @param ev the mouse event
   */
  @Override
  public void onMousePressed(MapMouseEvent ev) {
      //panePos = ev.getPoint();
      panning = true;
}
  
  /**
   * Respond to a mouse dragged event. Calls {@link org.geotools.swing.MapPane#moveImage()}
   *
   * @param ev the mouse event
   */
  @Override
  public void onMouseDragged(MapMouseEvent ev) {
      if (panning) {
          Point pos = ev.getPoint();
          System.out.println(pos.x + "," + pos.y);
          /*if (!pos.equals(panePos)) {
              getMapPane().moveImage(pos.x - panePos.x, pos.y - panePos.y);
              panePos = pos;
          }*/
      }
  }

  /**
   * If this button release is the end of a mouse dragged event, requests the map mapPane to
   * repaint the display
   *
   * @param ev the mouse event
   */
  @Override
  public void onMouseReleased(MapMouseEvent ev) {
      panning = false;
  }

  /** Get the mouse cursor for this tool */
  @Override
  public Cursor getCursor() {
      return cursor;
  }
  
  /**
   * Returns false to indicate that this tool does not draw a box on the map display when the
   * mouse is being dragged
   */
  @Override
  public boolean drawDragBox() {
      return false;
  }
}

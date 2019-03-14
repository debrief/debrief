
package org.mwc.debrief.lite.map;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.AbstractZoomTool;

import MWC.Algorithms.Conversions;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * A cursor tool to display the measured range and bearing between two points
 *
 * @author Ian Mayo - from Michael Bedward's Zoom In tool
 */
public class RangeBearingTool extends AbstractZoomTool
{

  /** Tool name */
  public static final String TOOL_NAME = "Rng/Brg";

  /** Tool tip text */
  public static final String TOOL_TIP =
      "Measure Range/Bearing between dragged points";

  /** Cursor */
  public static final String CURSOR_IMAGE =
      "/org/geotools/swing/icons/mActionIdentify.png";

  /** Cursor hotspot coordinates */
  public static final Point CURSOR_HOTSPOT = new Point(14, 9);

  /** Icon for the control */
  public static final String ICON_IMAGE =
      "/org/geotools/swing/icons/mActionPan.png";

  private final Cursor cursor;

  private WorldLocation startPos;

  private final JLabel _statusBar;

  /**
   * Constructor
   *
   * @param statusBar
   */
  public RangeBearingTool(final JLabel statusBar)
  {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final ImageIcon imgIcon = new ImageIcon(getClass().getResource(
        CURSOR_IMAGE));
    cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);
    _statusBar = statusBar;
  }

  /**
   * Returns true to indicate that this tool draws a box on the map display when the mouse is being
   * dragged to show the zoom-in area
   */
  @Override
  public boolean drawDragBox()
  {
    return false;
  }

  /**
   * Get the mouse cursor for this tool
   */
  @Override
  public Cursor getCursor()
  {
    return cursor;
  }

  /**
   * Task complete. No further action required.
   *
   * @param e
   *          map mapPane mouse event
   */
  @Override
  public void onMouseClicked(final MapMouseEvent e)
  {
    final Rectangle paneArea = ((JComponent) getMapPane()).getVisibleRect();
    final DirectPosition2D mapPos = e.getWorldPos();

    final double scale = getMapPane().getWorldToScreenTransform().getScaleX();
    final double newScale = scale * zoom;

    final DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d
        * paneArea.getWidth() / newScale, mapPos.getY() + 0.5d * paneArea
            .getHeight() / newScale);

    final Envelope2D newMapArea = new Envelope2D();
    newMapArea.setFrameFromCenter(mapPos, corner);
    getMapPane().setDisplayArea(newMapArea);
  }

  /**
   * Records that the mouse is being dragged
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseDragged(final MapMouseEvent ev)
  {
    // ok, sort out the range and bearing
    final DirectPosition2D curPos = ev.getWorldPos();
    final WorldLocation current = new WorldLocation(curPos.getY(), curPos
        .getX(), 0);

    // now the delta
    final WorldVector delta = current.subtract(startPos);
    final WorldDistance distance = new WorldDistance(delta.getRange(),
        WorldDistance.DEGS);
    double bearing = Conversions.Rads2Degs(delta.getBearing());
    if (bearing < 0)
    {
      bearing += 360d;
    }
    final String msg = "Range:" + (int) distance.getValueIn(
        WorldDistance.METRES) + "m Brg:" + (int) bearing + "°";
    if (_statusBar != null)
    {
      _statusBar.setText(msg);
    }
  }

  /**
   * Records the map position of the mouse event in case this button press is the beginning of a
   * mouse drag
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMousePressed(final MapMouseEvent ev)
  {
    final DirectPosition2D startPosWorld = ev.getWorldPos();
    startPos = new WorldLocation(startPosWorld.getY(), startPosWorld.getX(), 0);
  }

  /**
   * If the mouse was dragged, determines the bounds of the box that the user defined and passes
   * this to the mapPane's {@code setDisplayArea} method.
   *
   * @param ev
   *          the mouse event
   */
  @Override
  public void onMouseReleased(final MapMouseEvent ev)
  {
    startPos = null;
  }
}

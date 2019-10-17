
package org.mwc.debrief.lite.map;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.AbstractMapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.AbstractZoomTool;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import Debrief.GUI.Frames.Application;
import MWC.Algorithms.Conversions;
import MWC.GUI.ToolParent;
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

  public static class RangeBearingMeasure
  {
    private WorldDistance distance;
    private double bearing;
    private static final String DEGREE_SYMBOL = "\u00b0";
    
    public RangeBearingMeasure(WorldDistance distance, double bearing)
    {
      this.distance = distance;
      this.bearing = bearing;
    }
    
    public void normalizeBearing()
    {
      if (bearing < 0)
      {
        bearing += 360d;
      }
    }
    
    public String getLongFormat()
    {
      return "Range:" + (int) distance.getValueIn(
          WorldDistance.YARDS) + "yd Brg:" + (int) bearing + DEGREE_SYMBOL;
    }
    
    public String getShortFormat()
    {
      return (int) distance.getValueIn(
          WorldDistance.YARDS) + "yd " + (int) bearing + DEGREE_SYMBOL;
    }
    
    public int getIntBearing()
    {
      return (int)bearing;
    }
    
    public int getPrintBearing()
    {
      // +360 just in case... :)
      return (getIntBearing() + 360) % 180 - 90;
    }
  }
  
  /** Tool name */
  public static final String TOOL_NAME = "Rng/Brg";

  /** Tool tip text */
  public static final String TOOL_TIP =
      "Measure Range/Bearing between dragged points";

  /** Cursor */
  public static final String CURSOR_IMAGE =
      "/org/geotools/swing/icons/mActionIdentify.png";

  /** Cursor hotspot coordinates */
  public static final Point CURSOR_HOTSPOT = new Point(0, 0);

  /** Icon for the control */
  public static final String ICON_IMAGE =
      "/org/geotools/swing/icons/mActionPan.png";

  private final Cursor cursor;

  private WorldLocation startPos;

  private final JLabel _statusBar;

  private final MathTransform _transform;
  
  /**
   * Button that we have pressed. It is used to filter
   * multiple mouse clicking.
   */
  private int buttonPressed = -1;

  /**
   * True if it is getting dragged now
   */
  private boolean dragging = false;
  
  private final MouseDragLine dragLine;
  
  /**
   * Constructor
   *
   * @param statusBar
   */
  public RangeBearingTool(final JLabel statusBar, final MathTransform transform, final AbstractMapPane abstractMapPane)
  {
    final Toolkit tk = Toolkit.getDefaultToolkit();
    final ImageIcon imgIcon = new ImageIcon(getClass().getResource(
        CURSOR_IMAGE));
    cursor = tk.createCustomCursor(imgIcon.getImage(), CURSOR_HOTSPOT,
        TOOL_NAME);
    _statusBar = statusBar;
    _transform = transform;

    
    dragLine = new MouseDragLine(abstractMapPane);
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

    // mouse pos in Map coordinates
    if (ev.getWorldPos()
        .getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84)
    {
      try
      {
        _transform.transform(curPos, curPos);
      }
      catch (MismatchedDimensionException | TransformException e)
      {
        Application.logError2(ToolParent.ERROR,
            "Failure in projection transform", e);
      }
    }

    final WorldLocation current = new WorldLocation(curPos.getY(), curPos
        .getX(), 0);

    // now the delta
    final WorldVector delta = current.subtract(startPos);
    final WorldDistance distance = new WorldDistance(delta.getRange(),
        WorldDistance.DEGS);
    double bearing = Conversions.Rads2Degs(delta.getBearing());
    
    final RangeBearingMeasure rangeBearing = new RangeBearingMeasure(distance, bearing);
    rangeBearing.normalizeBearing();
    final String msg = rangeBearing.getLongFormat();
    if (_statusBar != null)
    {
      _statusBar.setText(msg);
    }
    
    // Now we draw the line
    dragLine.mouseDragged(ev, rangeBearing);
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
    if ( !dragging )
    {
      final DirectPosition2D startPosWorld = ev.getWorldPos();

      if (ev.getWorldPos()
          .getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84)
      {
        try
        {
          _transform.transform(startPosWorld, startPosWorld);
        }
        catch (MismatchedDimensionException | TransformException e)
        {
          Application.logError2(ToolParent.ERROR,
              "Failure in projection transform", e);
        }
      }

      startPos = new WorldLocation(startPosWorld.getY(), startPosWorld.getX(), 0);
      buttonPressed = ev.getButton();
      dragging = true;
      
      dragLine.mousePressed(ev);
    }
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
    if ( dragging && ev != null && ev.getButton() == buttonPressed )
    {
      dragging = false;
      buttonPressed = -1;
      startPos = null;
      
      dragLine.mouseReleased(ev);
    }
  }
}

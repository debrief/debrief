package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: GridPainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: GridPainter.java,v $
// Revision 1.17  2006/06/06 09:19:32  Ian.Mayo
// Give grids an editable name
//
// Revision 1.16  2006/05/25 14:10:38  Ian.Mayo
// Make plottables comparable
//
// Revision 1.15  2005/09/13 09:28:29  Ian.Mayo
// Eclipse tidying
//
// Revision 1.14  2005/09/13 09:16:07  Ian.Mayo
// Minor tidying
//
// Revision 1.13  2005/09/07 13:45:45  Ian.Mayo
// Minor tidying
//
// Revision 1.12  2005/05/19 14:46:48  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.11  2005/01/11 15:18:04  Ian.Mayo
// make the grid slightly thicker
//
// Revision 1.10  2004/10/25 09:02:48  Ian.Mayo
// Correctly label grid lines when in relative mode (non-angular units)
//
// Revision 1.9  2004/10/19 13:27:05  Ian.Mayo
// More refactoring to support override by local grid painter
//
// Revision 1.8  2004/10/19 11:13:16  Ian.Mayo
// Getting closer - our new origin is respected when using angular units
//
// Revision 1.7  2004/10/19 10:15:02  Ian.Mayo
// Add local grid support
//
// Revision 1.6  2004/10/19 08:28:20  Ian.Mayo
// Refactor to allow over-riding by LocalGridPainter
//
// Revision 1.5  2004/08/31 09:38:06  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.4  2004/05/25 14:46:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.3  2003/10/21 08:15:36  Ian.Mayo
// Tidily format distance grid labels, correctly plot long as long
//
// Revision 1.2  2003/10/17 14:52:57  Ian.Mayo
// Better drawing of distance-related units
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-07-04 11:00:55+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.5  2003-01-30 10:25:10+00  ian_mayo
// Check we only plot sensible grid lines
//
// Revision 1.4  2002-10-30 16:26:59+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:53+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:14+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-17 20:40:26+00  administrator
// Reflect switch to Duration/WorldDistance
//
// Revision 1.3  2002-01-17 14:49:09+00  administrator
// Reflect fact that distance now in WorldDistance units
//
// Revision 1.2  2001-08-21 12:06:03+01  administrator
// Make grids visible by default
//
// Revision 1.1  2001-08-01 13:00:42+01  administrator
// Add code and properties necessary for plotting labels on grid lines
//
// Revision 1.0  2001-07-17 08:46:29+01  administrator
// Initial revision
//
// Revision 1.5  2001-06-04 09:37:37+01  novatech
// don't plot grid points if they're too close
//
// Revision 1.4  2001-01-22 19:40:37+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.3  2001-01-22 14:49:00+00  novatech
// Projection.toWorld() always return the same object, so completely handle the TL corner before we start processing the BR corner
//
// Revision 1.2  2001-01-22 12:29:30+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:13  ianmayo
// initial version
//
// Revision 1.7  2000-09-21 09:06:46+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.6  2000-08-18 13:36:07+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.5  2000-08-11 08:41:59+01  ian_mayo
// tidy beaninfo
//
// Revision 1.4  2000-08-09 16:03:13+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.3  1999-11-26 15:45:32+00  ian_mayo
// adding toString methods
//
// Revision 1.2  1999-11-11 18:19:19+00  ian_mayo
// minor tidying up
//
// Revision 1.1  1999-10-12 15:37:01+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-09-14 14:17:55+01  administrator
// working with values other than whole degrees
//
// Revision 1.1  1999-08-17 10:02:08+01  administrator
// Initial revision
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.text.DecimalFormat;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class GridPainter implements Plottable, Serializable
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the colour for this grid
   */
  protected Color _myColor;

  /**
   * the grid separation (in degrees)
   */
  protected WorldDistance _myDelta;

  /**
   * whether this grid is visible
   */
  protected boolean _isOn;

  /**
   * are we plotting lat/long labels?
   */
  protected boolean _plotLabels = true;

  /**
   * the minimum space between grid lines
   */
  private int MIN_SEPARATION = 4;

  /**
   * our editor
   */
  transient protected Editable.EditorType _myEditor;

	private String _myName;

	public static String GRID_TYPE_NAME;

  /**
   * the formatter we use which only shows decimal places when they're present
   */
  private static final DecimalFormat _distanceFormatter = new DecimalFormat("0.##");

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public GridPainter()
  {
    _myColor = Color.darkGray;
    
    GRID_TYPE_NAME = "Grid";
		_myName = GRID_TYPE_NAME;

    setDelta(new WorldDistance(1, WorldDistance.DEGS));
    // make it visible to start with
    setVisible(true);
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void setVisible(boolean val)
  {
    _isOn = val;
  }

  public boolean getVisible()
  {
    return _isOn;
  }

  public void setColor(Color val)
  {
    _myColor = val;
  }

  public Color getColor()
  {
    return _myColor;
  }

  /**
   * set the delta for this grid
   *
   * @param val the size in minutes
   */
  public void setDelta(WorldDistance val)
  {
    _myDelta = val;
  }

  /**
   * get the delta for the grid
   *
   * @return the size in minutes
   */
  public WorldDistance getDelta()
  {
    return _myDelta;
  }


  /**
   * whether to plot the labels or not
   */
  public boolean getPlotLabels()
  {
    return _plotLabels;
  }

  /**
   * whether to plot the labels or not
   */
  public void setPlotLabels(boolean val)
  {
    _plotLabels = val;
  }


  /**
   * whether to plot the labels or not
   */
  public void setName(String name)
  {
    _myName = name;
  }

  public void paint(CanvasType g)
  {

    // check we are visible
    if (!_isOn)
      return;

    float oldLineWidth = g.getLineWidth();

    g.setLineWidth(1.0f);

    Dimension screenArea;

    // get the current screen area
    screenArea = g.getProjection().getScreenArea();

    // check that the projection is initialised
    if (screenArea == null)
      return;

    // set the current colour
    g.setColor(_myColor);

    // create data coordinates from the current corners of the screen
    WorldLocation checkProjection = new WorldLocation(g.toWorld(new Point(0, 0)));

    // check we have data
    if (checkProjection == null)
      return;

    // get the delta in degrees
    double deltaDegs = _myDelta.getValueIn(WorldDistance.DEGS);

    // ok, find the outer limits of the lines to plot
    WorldArea bounds = getOuterBounds(g, screenArea, deltaDegs);

    double maxLat = bounds.getTopLeft().getLat();
    double minLong = bounds.getTopLeft().getLong();
    double minLat = bounds.getBottomRight().getLat();
    double maxLong = bounds.getBottomRight().getLong();

    // keep track of the point separation: if they are closer than 3 pixels,
    // drop out
    Point lastPoint = null;

    // doDecide if we want to see the decimal portion of the plotted labels
    boolean plotDecimals;

    // is our delta less than 1 second in width?
    if (deltaDegs < 1d / 60d / 60d)
    {
      plotDecimals = true;
    }
    else
      plotDecimals = false;

    // just trim the latitude, to ensure we plot realistic lats
    minLat = Math.max(minLat, -90);
    maxLat = Math.min(maxLat, 90);

    int counter = 0;

    final WorldLocation gridOrigin = getGridLabelOrigin(bounds);
    int latGridCounterOffset = (int) ((gridOrigin.getLat() - minLat) / _myDelta.getValueIn(WorldDistance.DEGS));
    int longGridCounterOffset = (int) ((gridOrigin.getLong() - minLong) / _myDelta.getValueIn(WorldDistance.DEGS));

    // if we're using a local plot we need to decrement both of these offsets by one
    if (this.isLocalPlotting())
    {
      latGridCounterOffset--;
      longGridCounterOffset--;
    }

    ////////////////////////////////////////////////////////////
    // first draw the lines going across
    /////////////////////////////////////////////////////////////

    for (double thisLat = minLat;
         thisLat <= maxLat;
         thisLat += deltaDegs)
    {
      Point p3 = g.toScreen(new WorldLocation(thisLat,
                                              maxLong,
                                              0));

      // the delta in screen coordinates
      int dy = 0;

      if (lastPoint == null)
        lastPoint = new Point(p3);
      else
      {
        // find how far apart they are
        dy = lastPoint.y - p3.y;

        // check if we're too close
        if (dy < MIN_SEPARATION)
          return;

        lastPoint = new Point(p3);
      }

      g.drawLine(0, p3.y, screenArea.width, p3.y);

      /////////////////////////////////////////////////////////////
      // and the labels
      /////////////////////////////////////////////////////////////

      if (_plotLabels)
      {
        // so, we are going to plot a label in the middle of this grid line

        // check if the lines are too close for labels (we'll use 15 pixels)
        if (dy >= 15)
        {
          String val;
          if (_myDelta.isAngular())
          {
            // get the formatted label
            val = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLat(thisLat, plotDecimals);
          }
          else
          {
            // ok, use a counter for the units
            double thisVal = (counter++ - latGridCounterOffset) * _myDelta.getValueIn(WorldDistance.DEGS);
            val = _distanceFormatter.format(thisVal) + " " +
              _myDelta.getUnitsLabel();
          }
          // and output it
          g.drawText(val, 0, p3.y - 2);
        }
      }
    }

    // just trim the latitude, to ensure we plot realistic lats
    minLong = Math.max(minLong, -180);
    maxLong = Math.min(maxLong, 180);

    counter = 0;

    WorldVector symetricUnitOffset = null;
    if (!_myDelta.isAngular())
    {
      symetricUnitOffset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90), deltaDegs, 0);
    }

    /////////////////////////////////////////////////////////////
    // now draw the lines going down
    /////////////////////////////////////////////////////////////

    double thisLong = minLong;
    while (thisLong < maxLong)
    {
      // find out if this is one of our "special cases" where lines of long
      // are in yds (absolute) not degrees (adjusted)
      if (_myDelta.isAngular())
      {
        thisLong += deltaDegs;
      }
      else
      {

        WorldLocation thisLoc = new WorldLocation(minLat,
                                                  thisLong,
                                                  0);
        // move it across a bit
        thisLoc.addToMe(symetricUnitOffset);

        // and take a copy of it.
        thisLong = thisLoc.getLong();

      }

      Point p3 = g.toScreen(new WorldLocation(minLat,
                                              thisLong,
                                              0));

      int p3x = p3.x;
      int p3y = p3.y;
      Point p4 = g.toScreen(new WorldLocation(maxLat,
                                              thisLong,
                                              0));

      // just check that both ends of the line are visible
      if ((p3x >= 0) || (p4.x >= 0))
      {
        g.drawLine(p3x, p3y, p4.x, p4.y);


        /////////////////////////////////////////////////////////////
        // and the labels
        /////////////////////////////////////////////////////////////

        if (_plotLabels)
        {

          String thisLabel;
          if (_myDelta.isAngular())
          {
            // get the formatted label
            thisLabel = MWC.Utilities.TextFormatting.BriefFormatLocation.toStringLong(thisLong, plotDecimals);
          }
          else
          {
            // ok, use a counter for the units
            double thisVal = (counter++ - longGridCounterOffset) * _myDelta.getValueIn(WorldDistance.DEGS);
            thisLabel = _distanceFormatter.format(thisVal)
              + " " + _myDelta.getUnitsLabel();
          }

          // and output it
          g.drawText(thisLabel, p3x + 2, 15);
        }
      }
    }

    // and restore the line width
    g.setLineWidth(oldLineWidth);
    
  }

  /**
   * determine where to start counting our grid labels from
   *
   * @param bounds
   * @return
   */
  protected WorldLocation getGridLabelOrigin(WorldArea bounds)
  {
    return bounds.getBottomLeft();
  }

  /**
   * unfortunately we need to do some plotting tricks when we're doing a locally-origined grid.
   * This method is over-ridden by the LocalGrid to allow this
   *
   * @return
   */
  protected boolean isLocalPlotting()
  {
    return false;
  }

  /**
   * find the top, bottom, left and right limits to plot. We've refactored it to a child class
   * so that it can be overwritten
   *
   * @param g          the plotting converter
   * @param screenArea the visible screen area
   * @param deltaDegs  the grid separation requested
   * @return an area providing the coverage requested
   */
  protected WorldArea getOuterBounds(CanvasType g, Dimension screenArea, double deltaDegs)
  {
    // create data coordinates from the current corners of the screen
    WorldLocation topLeft = g.toWorld(new Point(0, 0));

    // create new corners just outside the current plot area, and clip
    // them to the nearest 'delta' value
    double maxLat1 = Math.ceil(topLeft.getLat() / deltaDegs) * deltaDegs;
    double minLong1 = (int) Math.floor(topLeft.getLong() / deltaDegs) * deltaDegs;

    // now for the bottom right
    WorldLocation bottomRight = g.toWorld(new Point(screenArea.width, screenArea.height));

    // create new corners just outside the current plot area, and clip
    // them to the nearest 'delta' value
    double maxLong1 = Math.ceil(bottomRight.getLong() / deltaDegs) * deltaDegs;
    double minLat1 = Math.floor(bottomRight.getLat() / deltaDegs) * deltaDegs;

    WorldArea bounds = new WorldArea(new WorldLocation(maxLat1, minLong1, 0),
                                     new WorldLocation(minLat1, maxLong1, 0));
    return bounds;
  }

  /**
   * whether the plotting algorithm should offset the origin to the nearest whole number of selected units
   *
   * @return yes/no
   */
  protected boolean offsetOrigin()
  {
    return true;
  }

  public MWC.GenericData.WorldArea getBounds()
  {
    // doesn't return a sensible size
    return null;
  }

  public double rangeFrom(MWC.GenericData.WorldLocation other)
  {
    // doesn't return a sensible distance;
    return INVALID_RANGE;
  }

  /**
   * return this item as a string
   */
  public String toString()
  {
    return getName();
  }

  public String getName()
  {
    return _myName;
  }

  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new GridPainterInfo(this);

    return _myEditor;
  }


	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		String myName = this.getName() + this.hashCode();
		String hisName = other.getName() + arg0.hashCode();
		return myName.compareTo(hisName);
	}  
  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class GridPainterInfo extends Editable.EditorType implements Serializable
  {

    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public GridPainterInfo(GridPainter data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("Color", "the Color to draw the grid", FORMAT),
          prop("Visible", "whether this grid is visible", VISIBILITY),
          prop("PlotLabels", "whether to plot grid labels", VISIBILITY),
          prop("Name", "name of this grid", FORMAT),
          prop("Delta", "the step size for the grid", VISIBILITY)
        };

        return res;
      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class GridPainterTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public GridPainterTest(String val)
    {
      super(val);
    }

    public void testMyParams()
    {
      MWC.GUI.Editable ed = new GridPainter();
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }


}


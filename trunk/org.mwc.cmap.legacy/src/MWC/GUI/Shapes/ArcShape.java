// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ArcShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ArcShape.java,v $
// Revision 1.7  2006/04/21 07:48:35  Ian.Mayo
// Make things draggable
//
// Revision 1.6  2004/10/25 08:38:18  Ian.Mayo
// remove d-line
//
// Revision 1.5  2004/10/20 08:36:24  Ian.Mayo
// Update Arc to allow filled plotting and painting in spoke arcs
//
// Revision 1.4  2004/10/19 15:14:56  Ian.Mayo
// Correctly calculate the when we're drawing the arc
//
// Revision 1.3  2004/10/19 15:03:38  Ian.Mayo
// Arc plotting/reading/writing complete
//
// Revision 1.2  2004/10/19 14:36:09  Ian.Mayo
// Refactor to inherit from Circle
//
// Revision 1.1  2004/10/19 14:15:29  Ian.Mayo
// All working fine, so far
//
// Revision 1.3  2004/08/31 09:38:14  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:10  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:33  Ian.Mayo
// Initial import
//
// Revision 1.12  2003-07-04 11:00:56+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.11  2003-07-03 14:59:53+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.10  2003-06-25 08:51:01+01  ian_mayo
// Only plot if we are visible
//
// Revision 1.9  2003-03-18 12:07:20+00  ian_mayo
// extended support for transparent filled shapes
//
// Revision 1.8  2003-03-03 11:54:34+00  ian_mayo
// Implement filled shape management
//
// Revision 1.7  2003-01-31 11:31:33+00  ian_mayo
// Remove duff method
//
// Revision 1.6  2003-01-23 16:04:29+00  ian_mayo
// provide methods to return the shape as a series of segments
//
// Revision 1.5  2003-01-21 16:31:57+00  ian_mayo
// Try 3-d support, move getColor property management to ShapeWrapper
//
// Revision 1.4  2002-11-01 14:44:01+00  ian_mayo
// minor tidying
//
// Revision 1.3  2002-10-30 16:27:01+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:22+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:07+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-03-19 11:04:05+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.1  2002-01-17 20:40:26+00  administrator
// Reflect switch to Duration/WorldDistance
//
// Revision 1.0  2001-07-17 08:43:15+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 19:40:36+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.2  2001-01-22 12:29:28+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:42:20+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:07  ianmayo
// initial version
//
// Revision 1.9  2000-09-21 09:06:48+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.8  2000-08-18 13:36:02+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.7  2000-08-14 15:49:29+01  ian_mayo
// tidy up descriptions
//
// Revision 1.6  2000-08-11 08:41:58+01  ian_mayo
// tidy beaninfo
//
// Revision 1.5  2000-02-14 16:50:44+00  ian_mayo
// Added boolean methods to allow user to specify "filled" or "not filled"
//
// Revision 1.4  1999-11-18 11:11:26+00  ian_mayo
// minor tidying up
//
// Revision 1.3  1999-11-11 18:19:04+00  ian_mayo
// allow for clicking around the edge of the circle
//
// Revision 1.2  1999-10-14 11:59:20+01  ian_mayo
// added property support and location editing
//
// Revision 1.1  1999-10-12 15:36:35+01  ian_mayo
// Initial revision
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ArcShape extends CircleShape
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  // keep track of versions
  static final long serialVersionUID = 1;

  /**
   * the centre bearing of the shape
   */
  private double _centreBearing;

  /**
   * the arc-width to use
   */
  private double _arcWidth;

  /**
   * the number of segments to use to plot this shape (when applicable)
   */
  public static final int NUM_SEGMENTS1 = 40;

  /**
   * whether to plot the origin of the arc
   */
  private boolean _plotOrigin = true;

  /**
   * whether to plot in the spokes of the arc
   */
  private boolean _plotSpokes = false;
  
  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * constructor
   *
   * @param theCentre the WorldLocation marking the centre of the circle
   * @param theRadius the radius of the circle (in yards)
   */
  public ArcShape(WorldLocation theCentre,
                  double theRadius,
                  double centreBearing,
                  double arcWidth,
                  boolean plotOrigin,
                  boolean plotSpokes)
  {
  	this(theCentre, new WorldDistance(theRadius, WorldDistance.YARDS), centreBearing, arcWidth, plotOrigin, plotSpokes);
  } 
  
  /**
   * constructor
   *
   * @param theCentre the WorldLocation marking the centre of the circle
   * @param theRadius the radius of the circle (in yards)
   */
  public ArcShape(WorldLocation theCentre,
                  WorldDistance theRadius,
                  double centreBearing,
                  double arcWidth,
                  boolean plotOrigin,
                  boolean plotSpokes)
  {
    super(theCentre, theRadius);

    super.setName("Arc");

    _centreBearing = centreBearing;
    _arcWidth = arcWidth;
    _plotOrigin = plotOrigin;
    _plotSpokes = plotSpokes;

    // now represented our arc as an area
    calcPoints();
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  public void paint(CanvasType dest)
  {
    // are we visible?
    if (!getVisible())
      return;

    if (this.getColor() != null)
    {
      // create a transparent colour
      Color newcol = getColor();
      dest.setColor(new Color(newcol.getRed(), newcol.getGreen(), newcol.getBlue(), TRANSPARENCY_SHADE));
    }

    double radDegs = _theRadius.getValueIn(WorldDistance.DEGS);
    WorldLocation topLeft = new WorldLocation(_theCentre.add(new WorldVector(0, radDegs, 0)));
    topLeft.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), radDegs, 0));

    // create & extend to bottom right
    WorldLocation bottomRight = new WorldLocation(_theCentre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180), radDegs, 0)));
    bottomRight.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90), radDegs, 0));


    // get the origin
    Point tl = dest.toScreen(topLeft);

    int tlx = tl.x;
    int tly = tl.y;

    // get the width and height
    Point br = dest.toScreen(bottomRight);

    // sort out the start angle
    int startAngle = (int) (_centreBearing - _arcWidth / 2d);

    // and offset by 90 degs
    startAngle -= 90;

    // and make it go anticlockwise
    startAngle = -startAngle;

    int wid = br.x - tlx;
    int height = br.y - tly;

    // and now draw it

    if (getFilled())
    {
      dest.fillArc(tlx, tly, wid, height, startAngle, (int) -_arcWidth);
    }
    else
    {
      dest.drawArc(tlx, tly, wid, height, startAngle, (int) -_arcWidth);
    }


    Point origin = new Point(dest.toScreen(_theCentre));


    // does the user want us to plot the origin?
    if (_plotOrigin)
    {
      // also plot the origin
      dest.fillRect(origin.x - 1, origin.y - 2, 3, 3);
    }

    // spokes?
    if (_plotSpokes)
    {
      Point startPoint = dest.toScreen(getStartPoint(radDegs));

      dest.drawLine(origin.x, origin.y, startPoint.x, startPoint.y);

      Point endPoint = dest.toScreen(getEndPoint(radDegs));

      dest.drawLine(origin.x, origin.y, endPoint.x, endPoint.y);
    }

  }


  /**
   * calculate some convenience values based on the radius
   * and centre of the circle
   */
  protected void calcPoints()
  {
    // calc the radius in degrees
    double radDegs = _theRadius.getValueIn(WorldDistance.DEGS);

    // create our area, starting with the centre point
    _theArea = new WorldArea(_theCentre, _theCentre);

    // extend to start of line
    WorldLocation other = getStartPoint(radDegs);
    _theArea.extend(other);

    // extend to centre of line
    WorldLocation other2 = getCentrePoint(radDegs);
    _theArea.extend(other2);

    // extend to centre of line
    WorldLocation other3 = getEndPoint(radDegs);
    _theArea.extend(other3);
  }

  /**
   * calculate the point at the start of the arc
   *
   * @param radDegs
   * @return
   */
  private WorldLocation getStartPoint(double radDegs)
  {
    double startAng = MWC.Algorithms.Conversions.Degs2Rads(_centreBearing - _arcWidth / 2);
    WorldLocation other = _theCentre.add(new WorldVector(startAng, radDegs, 0));
    return other;
  }

  /**
   * calculate the point half way along the arc
   *
   * @param radDegs
   * @return
   */
  private WorldLocation getCentrePoint(double radDegs)
  {
    double centre = MWC.Algorithms.Conversions.Degs2Rads(_centreBearing);
    WorldLocation other2 = _theCentre.add(new WorldVector(centre, radDegs, 0));
    return other2;
  }

  /**
   * calculate the location of the point at the end of the arc
   *
   * @param radDegs
   * @return
   */
  private WorldLocation getEndPoint(double radDegs)
  {
    double endAng = MWC.Algorithms.Conversions.Degs2Rads(_centreBearing + _arcWidth / 2);
    WorldLocation other3 = _theCentre.add(new WorldVector(endAng, radDegs, 0));
    return other3;
  }


  /**
   * get the range from the indicated world location -
   * making this abstract allows for individual shapes
   * to have 'hit-spots' in various locations.
   */
  public double rangeFrom(WorldLocation point)
  {
    double res = this._theCentre.rangeFrom(point);

    /** note, the user may also be clicking on the arc itself
     *
     */
    double radDegs =_theRadius.getValueIn(WorldDistance.DEGS);

    // first the start
    double res2 = getStartPoint(radDegs).rangeFrom(point);
    res = Math.min(res, res2);

    // now the centre
    res2 = getCentrePoint(radDegs).rangeFrom(point);
    res = Math.min(res, res2);

    // now the end
    res2 = getEndPoint(radDegs).rangeFrom(point);
    res = Math.min(res, res2);

    return res;
  }

  /**
   * the angular size of the arc (degs)
   *
   * @return angular size of arc
   */
  public double getArcWidth()
  {
    return _arcWidth;
  }

  /**
   * the angular size of the arc (degs)
   *
   * @param _arcWidth the size
   */
  public void setArcWidth(double _arcWidth)
  {
    this._arcWidth = _arcWidth;
    
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);    
  }

  /**
   * the centre of the arc (degs)
   *
   * @return angle from the origin to the centre
   */
  public double getCentreBearing()
  {
    return _centreBearing;
  }


  /**
   * the centre of the arc (degs)
   *
   * @param centreBearing angle from the origin to 1/2 way along line of arc
   */
  public void setCentreBearing(double centreBearing)
  {
    this._centreBearing = centreBearing;
    
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);    
    
  }

  /**
   * whether to plot the point at the origin
   *
   * @return yes/no
   */
  public boolean getPlotOrigin()
  {
    return _plotOrigin;
  }

  /**
   * whether to plot the point at the origin
   *
   * @param plotOrigin yes/no
   */
  public void setPlotOrigin(boolean plotOrigin)
  {
    this._plotOrigin = plotOrigin;
  }

  /**
   * whether to plot in the spokes of the arc
   *
   * @return yes/no
   */
  public boolean getPlotSpokes()
  {
    return _plotSpokes;
  }

  /**
   * whether to plot the spokes of an arc
   *
   * @param plotSpokes yes/no
   */
  public void setPlotSpokes(boolean plotSpokes)
  {
    this._plotSpokes = plotSpokes;
  }

  //////////////////////////////////////////////////
  // editor support
  //////////////////////////////////////////////////

  public boolean hasEditor()
  {
    return true;
  }

  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ArcInfo(this, this.getName());

    return _myEditor;
  }

  /**
   * get the 'anchor point' for any labels attached to
   * this shape
   */
  public WorldLocation getAnchor()
  {
    return _theCentre;
  }

  //////////////////////////////////////////////////
  // 3-d support
  //////////////////////////////////////////////////
  /**
   * calculate the shape as a series of WorldLocation points.  Joined up, these form a representation of the shape
   */
  public Collection<WorldLocation> getDataPoints()
  {
    // get ready to store the list
    Collection<WorldLocation> res = new Vector<WorldLocation>(0, 1);

    // convert the radius to degs
    double radDegs =_theRadius.getValueIn(WorldDistance.DEGS);


    for (int i = 0; i <= NUM_SEGMENTS1; i++)
    {
      // produce the current bearing
      double this_brg = (360.0 / NUM_SEGMENTS1 * i) / 180.0 * Math.PI;

      // create a new point at our indicated radius on the current bearing
      WorldLocation wl = new WorldLocation(_theCentre.add(new WorldVector(this_brg, radDegs, 0)));

      res.add(wl);
    }

    return res;

  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class ArcInfo extends EditorType
  {

    public ArcInfo(ArcShape data,
                   String theName)
    {
      super(data, theName, "");
    }

    public String getName()
    {
      return ArcShape.this.getName();
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("Radius", "the circle radius"),
          prop("Centre", "the centre of the circle"),
          prop("CentreBearing", "bearing from centre of circle to 1/2 way along arc"),
          prop("ArcWidth", "the angle of arc to plot"),
          prop("PlotOrigin", "whether to plot the origin of the arc"),
          prop("Filled", "whether to fill the arc"),
          prop("PlotSpokes", "whether to draw in the spokes of the arc")
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
  static public class CircleTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public CircleTest(String val)
    {
      super(val);
    }

    public void testMyParams()
    {
      Editable ed = new ArcShape(new WorldLocation(2d, 2d, 2d),new WorldDistance(2d, WorldDistance.YARDS), 12, 2, true, true);
      editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}





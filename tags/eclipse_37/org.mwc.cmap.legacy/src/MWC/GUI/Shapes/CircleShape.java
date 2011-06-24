// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CircleShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: CircleShape.java,v $
// Revision 1.7  2006/05/02 13:21:37  Ian.Mayo
// Make things draggable
//
// Revision 1.6  2006/04/21 07:48:36  Ian.Mayo
// Make things draggable
//
// Revision 1.5  2006/03/22 16:09:13  Ian.Mayo
// Tidying
//
// Revision 1.4  2004/10/19 14:36:32  Ian.Mayo
// Make guts more visible, to aid inheritance
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
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class CircleShape extends PlainShape implements Editable, HasDraggableComponents
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  // keep track of versions
  static final long serialVersionUID = 1;

  /**
   * the area covered by this circle
   */
  protected WorldArea _theArea;
  
  /** the shape, broken down into a series of points
   * 
   */
  protected Vector<WorldLocation> _myPoints = new Vector<WorldLocation>();


  /**
   * the centre of this circle
   */
  protected WorldLocation _theCentre;

  /**
   * the radius of this circle (in yards)
   */
  protected WorldDistance _theRadius;

  /**
   * our editor
   */
  transient protected Editable.EditorType _myEditor;

  /**
   * the number of segments to use to plot this shape (when applicable)
   */
  public static final int NUM_SEGMENTS = 40;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * constructor
   *
   * @param theCentre the WorldLocation marking the centre of the circle
   * @param theRadius the radius of the circle (in yards)
   */
  public CircleShape(WorldLocation theCentre, double theRadius)
  {
  	this(theCentre, new WorldDistance(theRadius, WorldDistance.YARDS));
  }
  
  /**
   * constructor
   *
   * @param theCentre the WorldLocation marking the centre of the circle
   * @param theRadius the radius of the circle (in yards)
   */
  public CircleShape(WorldLocation theCentre, WorldDistance theRadius)
  {
    super(0, 1, "Circle");

    // store the values
    _theCentre = theCentre;
    _theRadius = theRadius;

    // now represented our circle as an area
    calcPoints();
  	
  }

  //  public CircleShape(){
  //    // scrap, in case we are serializing
  //    _theArea = null;
  //  }

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

    // break the circle down into points
    final int STEPS = _myPoints.size();
    int[] xP = new int[STEPS];
    int[] yP = new int[STEPS];
    int ctr = 0;
    Iterator<WorldLocation> iter = _myPoints.iterator();
    while(iter.hasNext())
    {
      Point pt = dest.toScreen(iter.next());
      xP[ctr] = pt.x;
      yP[ctr++] = pt.y;
    }
    
    // and plot the polygon
    if (getFilled())
    {
    	dest.fillPolygon(xP, yP, STEPS);
    }
    else
    {
    	dest.drawPolygon(xP, yP, STEPS);
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
    
    // create our area
    _theArea = new WorldArea(_theCentre, _theCentre);

    // create & extend to top left
    WorldLocation other = _theCentre.add(new WorldVector(0, radDegs, 0));
    _theArea.extend(other);
    other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), radDegs, 0));
    _theArea.extend(other);

    // create & extend to bottom right
    other = _theCentre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180), radDegs, 0));
    _theArea.extend(other);
    other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90), radDegs, 0));
    _theArea.extend(other);
    
    // clear our local list of points
    _myPoints.removeAllElements();

    // and the circle as a series of points (so it turns properly in relative mode)
    final int STEPS = 100;
    for(int i=0;i<STEPS;i++)
    {
    	double thisAngle = (Math.PI * 2) / (double) STEPS * i;
      // create & extend to top left
      WorldLocation newPt = _theCentre.add(new WorldVector(thisAngle, radDegs, 0));
			_myPoints.add(newPt);
    }
  }

  public MWC.GenericData.WorldArea getBounds()
  {
    return _theArea;
  }

  /**
   * get the range from the indicated world location -
   * making this abstract allows for individual shapes
   * to have 'hit-spots' in various locations.
   */
  public double rangeFrom(WorldLocation point)
  {
    double res = this._theCentre.rangeFrom(point);

    /** note, also allow us to recognise that the user may
     * be clicking on the circle itself, so do a second
     * check using the difference between the range from the
     * centre of the circle and the radius of the circle
     */
    double res2 = Math.abs(_theRadius.getValueIn(WorldDistance.DEGS) - res);

    return Math.min(res, res2);
  }

  /**
   * return the radius of this circle
   *
   * @return the radius of this circle
   */
  public WorldDistance getRadius()
  {
    return _theRadius;
  }

  /**
   * set the centre location of the circle
   *
   * @param centre the WorldLocation marking the centre
   */
  public void setCentre(WorldLocation centre)
  {
    // inform our listeners
    firePropertyChange("Location", _theCentre, centre);
    // make the change
    _theCentre = centre;
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);    
  }

  /**
   * @return the centre of the circle
   */
  public WorldLocation getCentre()
  {
    return _theCentre;
  }

  public void setCircleColor(Color val)
  {
    super.setColor(val);
  }

  public Color getCircleColor()
  {
    return super.getColor();
  }

  /**
   * set the radius of this circle
   */
  public void setRadius(WorldDistance val)
  {
    _theRadius = val;
    
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);    
    
  }

  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new CircleInfo(this, this.getName());

    return _myEditor;
  }

  /**
   * get the 'anchor point' for any labels attached to
   * this shape
   */
  public MWC.GenericData.WorldLocation getAnchor()
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
    double radDegs = _theRadius.getValueIn(WorldDistance.DEGS);

    for (int i = 0; i <= NUM_SEGMENTS; i++)
    {
      // produce the current bearing
      double this_brg = (360.0 / NUM_SEGMENTS * i) / 180.0 * Math.PI;

      // create a new point at our indicated radius on the current bearing
      WorldLocation wl = new WorldLocation(_theCentre.add(new WorldVector(this_brg, radDegs, 0)));

      res.add(wl);
    }

    return res;

  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class CircleInfo extends Editable.EditorType
  {

    public CircleInfo(CircleShape data,
                      String theName)
    {
      super(data, theName, "");
    }

    public String getName()
    {
      return CircleShape.this.getName();
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("Radius", "the circle radius"),
          prop("Centre", "the centre of the circle"),
          prop("Filled", "whether to fill the circle")
        };

        return res;

      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }
  

	public void shift(WorldLocation feature, WorldVector vector)
	{
		// ok, just shift it...
		feature.addToMe(vector);
		
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);    
		
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		// right, see if the cursor is at the centre (that's the easy component)
		checkThisOne(_theCentre, cursorLoc, currentNearest, this, parentLayer);
		
		// now for the more difficult one. See if it is on the radius.
		// - how far is it from the centre
		WorldVector vec = cursorLoc.subtract(_theCentre);
		WorldDistance sep = new WorldDistance(vec);
		
		// ahh, now subtract the radius from this separation
		WorldDistance newSep = new WorldDistance(Math.abs(sep.getValueIn(WorldDistance.YARDS) - 
				this._theRadius.getValueIn(WorldDistance.YARDS)), WorldDistance.YARDS);
		
		// now we have to wrap this operation in a made-up location
		WorldLocation dragCentre = new WorldLocation(cursorLoc){
			private static final long serialVersionUID = 100L;
			
			public void addToMe(WorldVector delta)
			{
					// ok - process the drag
					super.addToMe(delta);
					// ok, what's this distance from the origin?
					WorldVector newSep1 = subtract(_theCentre);
					WorldDistance dist = new WorldDistance(newSep1);
					setRadius(dist);
			
		//		WorldDistance newDist = new WorldDistance(dist.getValueIn(WorldDistance.YARDS) + _theRadius, WorldDistance.YARDS);
				// hmm, are we going in or out?
				// now, change the radius to this
			}
			};
			// try range
			currentNearest.checkMe(this, newSep, null, parentLayer, dragCentre);

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
      MWC.GUI.Editable ed = new CircleShape(new WorldLocation(2d, 2d, 2d), 2d);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }

	public void shift(WorldVector vector)
	{
		WorldLocation oldCentre = getCentre();
		WorldLocation newCentre = oldCentre.add(vector);
		setCentre(newCentre);
		
    // and calc the new summary data
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);    
		
	}

}





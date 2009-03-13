// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PolygonShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: PolygonShape.java,v $
// Revision 1.7  2006/07/31 11:37:49  Ian.Mayo
// Allow polygon to stay open
//
// Revision 1.6  2006/06/01 08:55:56  Ian.Mayo
// Return the actual polygon, since our new SWT polygon editor will be editing the actual item
//
// Revision 1.5  2006/05/18 09:34:04  Ian.Mayo
// Make drag-compliant
//
// Revision 1.4  2006/04/21 07:48:37  Ian.Mayo
// Make things draggable
//
// Revision 1.3  2004/08/31 09:38:17  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:16  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:33  Ian.Mayo
// Initial import
//
// Revision 1.10  2003-07-04 11:00:58+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.9  2003-07-03 14:59:52+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.8  2003-06-25 08:51:00+01  ian_mayo
// Only plot if we are visible
//
// Revision 1.7  2003-03-18 12:07:18+00  ian_mayo
// extended support for transparent filled shapes
//
// Revision 1.6  2003-03-03 11:54:32+00  ian_mayo
// Implement filled shape management
//
// Revision 1.5  2003-01-30 10:24:54+00  ian_mayo
// Implement the getDataPoints method
//
// Revision 1.4  2003-01-21 16:32:12+00  ian_mayo
// move getColor property management to ShapeWrapper
//
// Revision 1.3  2002-10-30 16:26:58+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.2  2002-05-28 09:25:52+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:53+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 14:01:08+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-03-19 11:04:05+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.1  2002-03-12 15:30:21+00  administrator
// Add custom class which creates custom version of Path editor - which labels paths as polygons
//
// Revision 1.0  2002-02-25 13:19:32+00  administrator
// Initial revision
//


package MWC.GUI.Shapes;

import java.awt.*;
import java.beans.*;
import java.io.Serializable;
import java.util.*;

import MWC.GUI.*;
import MWC.GUI.Properties.Swing.SwingWorldPathPropertyEditor;
import MWC.GenericData.*;

public class PolygonShape extends PlainShape implements Serializable, Editable, DraggableItem, HasDraggableComponents
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the area covered by this polygon
   */
  private WorldArea _theArea;

  /**
   * the points representing the shape
   */
  private MWC.GenericData.WorldPath _polygon;

  /**
   * our editor
   */
  transient private Editable.EditorType _myEditor;

  /**
   * the "anchor" which labels connect to
   */
  private WorldLocation _theAnchor;
  
  /** whether to join the ends of the polygon
   * 
   */
  private boolean _closePolygon = true;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /**
   * constructor
   *
   * @param polygon the WorldLocation marking the centre of the polygon
   */
  public PolygonShape(WorldPath polygon)
  {
    super(0, 1, "Polygon");

    // store the values
    _polygon = polygon;

    // now represented our polygon as an area
    calcPoints();
  }

  //  public PolygonShape(){
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

    // check we have some points
    if (_polygon == null)
      return;

    if (_polygon.size() > 0)
    {
      // create our point lists
      int[] xP = new int[_polygon.size()];
      int[] yP = new int[_polygon.size()];

      // ok, step through the area
      Iterator<WorldLocation> points = _polygon.getPoints().iterator();

      int counter = 0;

      while (points.hasNext())
      {
        WorldLocation next = (WorldLocation) points.next();

        // convert to screen
        Point thisP = dest.toScreen(next);

        // remember the coords
        xP[counter] = thisP.x;
        yP[counter] = thisP.y;

        // move the counter
        counter++;
      }

      // ok, now plot it
      if (getFilled())
      {
        dest.fillPolygon(xP, yP, xP.length);
      }
      else
      {
      	if(getClosed())
      		dest.drawPolygon(xP, yP, xP.length);
      	else
      		dest.drawPolyline(xP, yP, xP.length);
      }
    }    
    
    // unfortunately we don't have a way of tracking edits to the underlying
    // worldpath object (since the polygon editor manipulates it directly.
    // so, we'll recalc our bounds at each repaint.
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);    
    
  }

  /**
   * get the shape as a series of WorldLocation points.
   * Joined up, these form a representation of the shape
   */
  public Collection<WorldLocation> getDataPoints()
  {
    return this._polygon.getPoints();
  }

  /**
   * calculate some convenience values based on the radius
   * and centre of the polygon
   */
  protected void calcPoints()
  {
    // check we have some points
    if (_polygon == null)
      return;


    if (_polygon.size() > 0)
    {
      // running total of lat/longs which we can average to determine the centre
      double lats = 0;
      double longs = 0;

      // reset the area object
      _theArea = null;

      // ok, step through the area
      Iterator<WorldLocation> points = _polygon.getPoints().iterator();
      while (points.hasNext())
      {
        WorldLocation next = (WorldLocation) points.next();
        // is this our first point?
        if (_theArea == null)
        // yes, initialise the area
          _theArea = new WorldArea(next, next);
        else
        // no, just extend it
          _theArea.extend(next);

        lats += next.getLat();
        longs += next.getLong();
      }

      // ok, now produce the centre
      _theAnchor = new WorldLocation(lats / _polygon.size(), longs / _polygon.size(), 0);
    }
  }

  public WorldArea getBounds()
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
    double res = -1;

    if (_polygon.size() > 0)
    {
      // ok, step through the area
      Iterator<WorldLocation> points = _polygon.getPoints().iterator();
      while (points.hasNext())
      {
        WorldLocation next = (WorldLocation) points.next();

        double thisD = next.rangeFrom(point);

        // is this our first point?
        if (res == -1)
        {
          res = thisD;
        }
        else
          res = Math.min(res, thisD);
      }
    }

    return res;
  }


  /**
   * the points representing the polygon
   */
  public WorldPath getPoints()
  {
    // note, we have to return a fresh copy of the polygon
    // in order to know if an edit has been made the editor cannot edit the original
  	
  	// NO - IGNORE THAT.  We give the editor the actual polygon, since the PolygonEditorView
  	// will be changing the real polygon.  that's all.
  	
    return _polygon;//new WorldPath(_polygon);
  }

  /**
   * the points representing the polygon
   */
  public void setPoints(WorldPath poly)
  {
    _polygon = poly;

    // and recalc the points
    calcPoints();
    
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);
  }


  public void setPolygonColor(Color val)
  {
    super.setColor(val);
  }

  public Color getPolygonColor()
  {
    return super.getColor();
  }

  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new PolygonInfo(this, this.getName());

    return _myEditor;
  }

  /**
   * get the 'anchor point' for any labels attached to
   * this shape
   */
  public WorldLocation getAnchor()
  {
    return _theAnchor;
  }


  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class PolygonInfo extends Editable.EditorType
  {

    public PolygonInfo(PolygonShape data,
                       String theName)
    {
      super(data, theName, "");
    }

    public String getName()
    {
      return PolygonShape.this.getName();
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("Points", "the points representing the polygon"),
          prop("Filled", "whether to fill the polygon"),
          prop("Closed", "whether to close the polygon (ignored if filled)")
        };
        res[0].setPropertyEditorClass(PolygonShape.PolygonPathEditor.class);
        return res;

      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }
  

	public void shift(WorldVector vector)
	{
		// ok, cycle through the points, moving each one
		Collection<WorldLocation> pts = _polygon.getPoints();
		Iterator<WorldLocation> iter = pts.iterator();
		while(iter.hasNext())
		{
			WorldLocation pt = (WorldLocation) iter.next();
			WorldLocation newLoc = pt.add(vector);
			pt.setLat(newLoc.getLat());
			pt.setLong(newLoc.getLong());
			pt.setDepth(newLoc.getDepth());			
		}
		
		// and update the outer bounding area
		calcPoints();
		
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);		
	}  

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class PolygonTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public PolygonTest(String val)
    {
      super(val);
    }

    public void testMyParams()
    {
      Editable ed = new PolygonShape(new WorldPath(new WorldLocation[]{new WorldLocation(1, 1, 1), new WorldLocation(1, 2, 1)}));
      Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
    
  }


  /**
   * **************************************************************
   * embedded class which contains extended path editor, which
   * renames a "Path" as a "Polygon"
   * **************************************************************
   */
  public static class PolygonPathEditor extends SwingWorldPathPropertyEditor
  {
    /**
     * over-ride the type returned by the path editor
     */
    protected String getMyType()
    {
      return "Polygon";
    }
  }


	public void shift(WorldLocation feature, WorldVector vector)
	{
		// ok, just shift it...
		feature.addToMe(vector);
		calcPoints();
		
    // and inform the parent (so it can move the label)
		firePropertyChange("Location", null, null);
		
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		// ok - pass through our corners
		Iterator<WorldLocation> myPts = _polygon.getPoints().iterator();
		while(myPts.hasNext())
		{
			WorldLocation thisLoc = (WorldLocation) myPts.next();
			// right, see if the cursor is at the centre (that's the easy component)
			checkThisOne(thisLoc, cursorLoc, currentNearest, this, parentLayer);
		}

	}

	public boolean getClosed()
	{
		return _closePolygon;
	}

	public void setClosed(boolean polygon)
	{
		_closePolygon = polygon;
	}	  
}





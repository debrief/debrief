package MWC.GUI.Tools.Chart;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: MoveableDragger.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: MoveableDragger.java,v $
// Revision 1.5  2004/10/07 14:23:13  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/03 15:13:25  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.3  2004/08/31 08:05:10  Ian.Mayo
// Rename/remove old tests, so that we don't have non-testing classes whose named ends with Test (in support of Maven integration)
//
// Revision 1.2  2004/05/25 15:43:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-06-09 09:23:22+01  ian_mayo
// refactored to remove extra rubberband parameter in chart drag listener call
//
// Revision 1.3  2003-02-07 09:49:12+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:42+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-11-14 19:54:15+00  administrator
// Lots of improvements
//
// Revision 1.0  2001-07-17 08:42:56+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:19  ianmayo
// initial version
//
// Revision 1.4  2000-11-02 16:44:35+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.3  2000-03-14 09:54:53+00  ian_mayo
// use icons for these tools
//
// Revision 1.2  2000-02-03 15:08:19+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.1  1999-10-12 15:36:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:04:32+01  administrator
// Initial revision
//

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Moveable;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Rubberband;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainDragTool;
import MWC.GenericData.WorldLocation;

public class MoveableDragger extends PlainDragTool implements Serializable,
  PlainChart.ChartCursorMovedListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the chart we are watching
   */
  PlainChart _theChart;

  /**
   * the previous listener, so that we can re-install it
   */
  transient PlainChart.ChartDragListener _oldListener;

  /**
   * the object currently being moved
   */
  transient Moveable _movingObject;

  /**
   * the thing being dragged
   */
  transient boolean _dragging = false;

  /**
   * our rubberband thingy
   */
  protected Rubberband _myRubber = new MWC.GUI.RubberBanding.RubberbandDrag();

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public MoveableDragger(PlainChart theChart,
                         ToolParent theParent,
                         String theLabel)
  {
    super(theChart, theParent, theLabel, null);

    _theChart = theChart;
  }

  //  /**
  //   *  do we really need this?
  //   */
  //  public MoveableDragger()
  //  {
  //    // default constructor, used for serialisation
  //    super();
  //  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * cursor is moving, see if it is hitting anything
   */
  public void cursorMoved(WorldLocation thePos, boolean dragging, Layers theData)
  {
    // see if we are already in a 'drag' movement
    if (_dragging)
      return;

    // see if the chart in already in a dragging movement (by somebody else)
    if (dragging)
      return;

    // convert this to screen
    Moveable res = null;
    // find the nearest editable item
    RightClickEdit.ObjectConstruct vals = new RightClickEdit.ObjectConstruct();
    int num = theData.size();
    for (int i = 0; i < num; i++)
    {
      Layer thisL = theData.elementAt(i);
      if (thisL.getVisible())
      {
        // find the nearest items, this method call will recursively pass down through
        // the layers
        findNearestMoveable(thisL, thePos, vals);
      }
    }

    // retrieve the results
    res = (Moveable) vals.object;
    // see if the nearest moveable is within range
    if (res != null)
    {
      if (HitTester.doesHit(thePos, res.getBounds(), 20, _theChart.getCanvas().getProjection()))
      {
        // get the existing listener
        PlainChart.ChartDragListener cd = _theChart.getChartDragListener();

        // remember what we are moving
        _movingObject = res;

        // set the cursor
        super.setCursor(Cursor.CROSSHAIR_CURSOR);

        // is it somebody other than me?
        if (cd != this)
        {

          // remember it
          _oldListener = cd;

          // add us as the listener
          _theChart.setChartDragListener(this);

        }
      }
      else
      {
        // clear the pointer to the object we're dragging
        _movingObject = null;

        // cancel setting the cursor
        super.setCursor(Cursor.DEFAULT_CURSOR);
      }
    }

    // see if we have failed the hit test
    if (_movingObject != res)
    {

      // see if we are currently the listener
      if (_theChart.getChartDragListener() == this)
      {
        _theChart.removeChartDragListener(this);

        // restore the old lister
        if (_oldListener != null)
        {
          _theChart.setChartDragListener(_oldListener);
        }

        super.restoreCursor();
      }

      // we've missed the objects, so clear our target
      _movingObject = null;

    }

  }


  /**
   * return my rubber band
   */
  public MWC.GUI.Rubberband getRubberband()
  {
    return _myRubber;
  }

  /**
   * drag operation is complete - move the object
   */
  public void areaSelected(MWC.GenericData.WorldLocation theLocation, Point thePoint)
  {
    super.areaSelected(theLocation, thePoint);

    // see if we have selected a worthwhile area
    Rectangle rt = new Rectangle(_theStartPoint);
    rt.add(_theEndPoint);

    // check we still have our object
    if (_movingObject != null)
    {

      WorldLocation oldL = _movingObject.getLocation();

      // do the operation
      super.doExecute(new MoveableDraggerAction(_theChart,
                                                _movingObject,
                                                oldL,
                                                _theEnd));

      if (_oldListener != null)
      {
        // adn replace the old listener
        getChart().setChartDragListener(_oldListener);
      }

      _movingObject = null;
    }

    _dragging = false;
  }


  /**
   * we don't really do an aciton
   */
  public Action getData()
  {
    return null;
  }

  public void execute()
  {
    // add ourselves as a moved listener
    getChart().addCursorMovedListener(this);
  }

  /**
   * we have been de-selected, release the drag and replace the rubber
   */
  public void finish()
  {
    // make sure we aren't listening for drags
    getChart().removeChartDragListener(this);

    // stop listening for cursor movement
    getChart().removeCursorMovedListener(this);

    // do we still have the old one?
    if (_oldListener != null)
    {
      getChart().setChartDragListener(_oldListener);
    }
  }

  /**
   * so, we have now started moving, hooray
   */
  public void startMotion()
  {
    // store the current area
    _dragging = true;
  }

  public static void findNearestMoveable(Layer thisLayer,
                                         MWC.GenericData.WorldLocation cursorPos,
                                         RightClickEdit.ObjectConstruct currentNearest)
  {
    // so, step through this layer
    if (thisLayer.getVisible())
    {
      // go through this layer
      Enumeration<Editable> enumer = thisLayer.elements();
      while (enumer.hasMoreElements())
      {
        Plottable p = (Plottable) enumer.nextElement();

        // is this item draggable?
        if (p instanceof Moveable)
        {
          // how far away is it?
          double rng = p.rangeFrom(cursorPos);

          // does it return a range?
          if (rng != -1.0)
          {
            // has our results object been initialised?
            if (currentNearest.object == null)
            {
              // no, just copy in the data
              currentNearest.setData(p, rng, thisLayer);
            }
            else
            {
              // yes it has, copy the data items in
              if (rng < currentNearest.distance)
              {
                currentNearest.setData(p, rng, thisLayer);
              }
            }
          }
        }
        else
        {
          // is this item a layer itself?
          if (p instanceof Layer)
          {
            // cast to Layer
            Layer l = (Layer) p;

            // find the nearest values
            findNearestMoveable(l, cursorPos, currentNearest);
          }
        } // whether this is moveable
      }
    }
  }



  //////////////////////////////////////////////////
  // the data for the action
  ///////////////////////////////////////////////////

  protected class MoveableDraggerAction implements Action
  {

    private PlainChart _theChart1;
    private WorldLocation _oldLocation;
    private WorldLocation _newLocation;
    private Moveable _theMoveable;


    public MoveableDraggerAction(PlainChart theChart,
                                 Moveable theMoveable,
                                 WorldLocation oldLocation,
                                 WorldLocation newLocation)
    {
      _theMoveable = theMoveable;
      _theChart1 = theChart;
      _oldLocation = oldLocation;
      _newLocation = newLocation;
    }

    public boolean isRedoable()
    {
      return true;
    }

    public boolean isUndoable()
    {
      return true;
    }

    public String toString()
    {
      return "Move item operation";
    }

    public void undo()
    {
      // put back to the old location
      _theMoveable.doMove(_newLocation, _oldLocation);

      // and redraw
      _theChart1.update();
    }

    public void execute()
    {
      // move to the new location
      _theMoveable.doMove(_oldLocation, _newLocation);

      // and redraw
      _theChart1.update();
    }
  }

}
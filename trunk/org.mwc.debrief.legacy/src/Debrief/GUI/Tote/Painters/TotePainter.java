package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TotePainter.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.5 $
// $Log: TotePainter.java,v $
// Revision 1.5  2007/04/16 09:48:08  ian.mayo
// Remove debug lines, slight JDK1.5 syntax updates (generics)
//
// Revision 1.4  2005/12/13 09:04:26  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/25 10:24:06  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 14:05:03  Ian.Mayo
// Replace variable name previously used for counting through enumeration - now part of JDK1.5
//
// Revision 1.1.1.2  2003/07/21 14:47:24  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.7  2003-07-04 10:59:21+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.6  2003-03-21 15:44:02+00  ian_mayo
// Correctly set the line width before each redraw, and don't paint highlight to WMF file
//
// Revision 1.5  2003-03-19 15:37:51+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-07 09:02:35+00  ian_mayo
// Remove unnecessary
//
// Revision 1.3  2002-07-10 14:59:24+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.2  2002-05-28 12:28:03+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:16+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:02+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-17 15:03:57+00  administrator
// Reflect new interface to hide StepperListener class
//
// Revision 1.2  2001-10-03 16:06:20+01  administrator
// Rename cursor to display
//
// Revision 1.1  2001-10-01 12:49:47+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.0  2001-07-17 08:41:39+01  administrator
// Initial revision
//
// Revision 1.9  2001-04-11 17:18:59+01  novatech
// correctly handle what to do when we have a valid primary track, but we don't have a valid data point for it (such as when we are before a time-related annotation has appeared)
//
// Revision 1.8  2001-02-01 09:30:05+00  novatech
// correctly reflect usage of -1 as null time
//
// Revision 1.7  2001-01-24 12:12:52+00  novatech
// initialise lastDTG value, so that we can switch to snail mode without a time on the tote
//
// Revision 1.6  2001-01-24 11:35:52+00  novatech
// recognise optimised toScreen handling which reduces object creation
//
// Revision 1.5  2001-01-22 12:30:04+00  novatech
// added JUnit testing code
//
// Revision 1.4  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.3  2001-01-17 09:45:36+00  novatech
// tidily handle plotting symbols
//
// Revision 1.2  2001-01-15 11:20:14+00  novatech
// store data as hashmap (so that we can remember the name of the track aswell as the fix)
//
// Revision 1.1  2001-01-03 13:40:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:54  ianmayo
// initial import of files
//
// Revision 1.28  2000-10-24 11:22:27+01  ian_mayo
// don't create internal secondary highlighter -> retrieve it from the tote
//
// Revision 1.27  2000-10-09 13:37:30+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.26  2000-10-03 14:18:01+01  ian_mayo
// correctly handle the different primary/secondary track highlighters
//
// Revision 1.25  2000-09-27 14:46:53+01  ian_mayo
// name changes
//
// Revision 1.24  2000-09-26 09:54:08+01  ian_mayo
// provide support for relative plots
//
// Revision 1.23  2000-09-22 11:44:02+01  ian_mayo
// insert comments
//
// Revision 1.22  2000-09-21 09:05:19+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.21  2000-09-18 09:14:23+01  ian_mayo
// GUI name changes
//
// Revision 1.20  2000-08-18 13:34:06+01  ian_mayo
// Editable.EditorType
//
// Revision 1.19  2000-08-14 15:50:03+01  ian_mayo
// GUI name changes
//
// Revision 1.18  2000-08-11 08:40:59+01  ian_mayo
// tidy beaninfo
//
// Revision 1.17  2000-07-07 09:58:45+01  ian_mayo
// change panel name to prevent conflict
//
// Revision 1.16  2000-06-06 12:43:47+01  ian_mayo
// replot full diagram, not just small areas (to overcome problem in JDK1.3)
//
// Revision 1.15  2000-05-22 10:06:24+01  ian_mayo
// handle instances where current time is outside the time period of a particular track
//
// Revision 1.14  2000-04-19 11:24:46+01  ian_mayo
// allow WatchableList to return null values, which won't be plotted
//
// Revision 1.13  2000-04-03 10:48:03+01  ian_mayo
// only plot Watchable if it is currently visible
//
// Revision 1.12  2000-03-27 14:44:21+01  ian_mayo
// redraw the chart after we have been selected
//
// Revision 1.11  2000-03-14 09:53:54+00  ian_mayo
// further implementation/tidying up
//
// Revision 1.10  2000-03-09 09:26:17+00  ian_mayo
// provided accessor to expose "inRepaint" property
//
// Revision 1.9  2000-03-08 16:23:08+00  ian_mayo
// represent Tote rectangle size as bounded integer
//
// Revision 1.8  2000-03-07 14:48:12+00  ian_mayo
// optimised algorithms
//
// Revision 1.7  2000-02-02 14:27:10+00  ian_mayo
// remove d-lines
//
// Revision 1.6  2000-01-12 15:40:19+00  ian_mayo
// added concept of contacts
//
// Revision 1.5  1999-12-13 10:36:40+00  ian_mayo
// removed some d-lines
//
// Revision 1.4  1999-12-03 14:40:43+00  ian_mayo
// only show highlights on watchables assigned in tote
//
// Revision 1.3  1999-12-02 09:46:49+00  ian_mayo
// take list of watchables from tote, instead of using items from "Tracks" layer
//
// Revision 1.2  1999-11-26 15:51:41+00  ian_mayo
// tidying up
//
// Revision 1.1  1999-10-12 15:34:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:03:12+01  administrator
// Initial revision
//

import Debrief.GUI.Tote.*;
import Debrief.Tools.Tote.*;
import Debrief.Wrappers.*;
import Debrief.GUI.Tote.Painters.Highlighters.*;
import MWC.GUI.*;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.Algorithms.*;
import MWC.GenericData.*;
import MWC.GUI.Properties.BoundedInteger;
import java.util.*;
import java.awt.*;
import java.beans.*;


/** Class to provide "normal" highlighting, triggered by the stepper control.
 * This class just puts a white rectangle around the point being highlighted.
 */
public class TotePainter implements StepperListener,
                                    CanvasType.PaintListener,
                                    Editable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** the chart we are plotting to
   */
  final PlainChart _theChart;

  /** the information we are plotting
   */
  final Layers _theData;

  /** whether this was the first step we tool
   */
  boolean _firstStep;

  /** the last DTG we drew (initialise to NULL value) on construction
   */
  HiResDate _lastDTG = null;

  /** the color to draw myself
   */
  private Color _myColor = Color.white;

  /** the size to draw myself
   */
  private int _mySize = 5;

	/** the tote whose data we are plotting.
	 * We keep track of this to determine which
	 * tracks we are highlighting
	 */
  final AnalysisTote _theTote;

	/** keep track of the area covered by the
	 * updates we are creating
	 */
  java.awt.Rectangle _areaCovered;

	/** HACK: recognise if we are in a screen repaint event
	 */
  boolean _inRepaint;

  /** our editor
   */
  transient private Editable.EditorType _myEditor;

  /** the last set of items we highlighted
   */
	transient private HashMap<Watchable, WatchableList> oldHighlights = null;
	
  /** the old primary point plotted
   */
	transient private Watchable oldPrimary = null;

  /** the thickness of the marker line
   *
   */
  private final float MARKER_THICKNESS = 2.0f;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
/** constructor, of course
 * @param theChart the chart we will plot onto
 * @param theData the data we will read from (to find the list of things
 * to plot, aswell as the nearest point to the indicated time)
 * @param theTote the Tote itself, which we use to determine which are
 * the primary and secondary tracks to highlight
 */
  public TotePainter(final PlainChart theChart,
                     final Layers theData,
                     final AnalysisTote theTote)
  {
    // remember the chart
    _theChart = theChart;

    // remember the data
    _theData = theData;

		// remember the step control
		_theTote = theTote;

    // initialise the painter
    _firstStep = true;

		// initialise the area
		_areaCovered = null;

		_inRepaint = false;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

/** change the colour of the highlight
 * @param val the new colour
 */
  public final void setColor(final Color val)
  {_myColor = val;}

/** change the size of the highlight to plot
 * @param val the new size (stored with its constraints)
 */
  public final void setSize(final BoundedInteger val)
  { _mySize = val.getCurrent(); }

/** return the current highlight colour
 * @return the colour
 */
  public final Color getColor()
  { return _myColor; }

/** return the current size of the highlight
 * @return current size, stored with it's constraints
 */
  public final BoundedInteger getSize()
  { return new BoundedInteger(_mySize, 1, 20);  }


/** we have switched to a differed tote highlighter
 * @param on whether we are being switched on or off
 */
  public void steppingModeChanged(final boolean on)
  {
		if(on)
		{
			// say we are a painter
			_theChart.getCanvas().addPainter(this);

      // and redraw the chart
      _theChart.update();

		}
		else
		{
			_theChart.getCanvas().removePainter(this);
		}

  }


/** screen update
 * @param dest canvas to paint to
 */
  public final void paintMe(final CanvasType dest)
  {
    // just check that we're not writing to the WMF, 'cos
    // we don't want our marker point drawn to it
    if(dest instanceof MetafileCanvas)
    {
      // don't bother doing any more
      return;
    }

    // highlight the current (recent) track point
		_inRepaint = true;
		_firstStep = true;
    this.newTime(null, _lastDTG, dest);
		_inRepaint = false;

  }

/** return the area covered - not necessary
 * @return null, don't bother
 */
  public WorldArea getDataArea()
  {
    return null;
  }

/** ignore
 * @param theProj ignore
 * @param newScreenArea ignore
 */
  public final void resizedEvent(final PlainProjection theProj, final Dimension newScreenArea)
  {}

/** the name of this painter
 * @return our name
 */
  public String getName()
  { return "Time Highlighter"; }



	/** following a time step, this method draws a highlight around the
 * "current" points on the primary and secondary tracks.
 * @param oldDTG last time value
   * @param newDTG new time value
   * @param canvas plotting destination
   */
  public void newTime(final HiResDate oldDTG,
                      final HiResDate newDTG,
                      CanvasType canvas)
  {
    // check we have a valid new DTG
    if(newDTG == null)
      return;

		// check that there is at least one track on the plot
		if(_theTote.getPrimary() == null)
			return;

    // initialise flag to handle if the primary highlighter is symbols - if so, we
    // use it for both tracks
    boolean plottingSymbols = false;
    if(this.getCurrentPrimaryHighlighter() instanceof Debrief.GUI.Tote.Painters.Highlighters.SymbolHighlighter)
    {
      plottingSymbols = true;
    }

		// initialise the area covered
		_areaCovered = null;

		// first build up our list of points to highlight, then plot them,
		// so that we are using our "borrowed" graphics object for the
		// shortest time period
		final HashMap<Watchable, WatchableList> newHighlights = new HashMap<Watchable, WatchableList>();

		// check that tracks have been defined
		// see if we need to initialise the old vector
		if(oldPrimary == null)
		{
      /** there is a chance that we already have an oldHighlights object -
       * since there may be a primary track assigned, but that no points
       * were visible, in which case we don't need to re-create the old
       * highlights track */
      if(oldHighlights == null)
			  oldHighlights = new HashMap<Watchable, WatchableList>();

      final Debrief.Tools.Tote.Watchable[] list = _theTote.getPrimary().getNearestTo(oldDTG);
      if(list.length > 0)
        oldPrimary = list[0];
		}

    // find the point on the primary track which is nearest to the new point
    Debrief.Tools.Tote.Watchable[] list = _theTote.getPrimary().getNearestTo(newDTG);

		Watchable newPrimary = null;
    if(list.length > 0)
        newPrimary = list[0];

    // so, step through the participants
		final Vector<WatchableList> theParticipants = _theTote.getSecondary();

		if(theParticipants != null)
		{
			// the watchables are used as keys in the hashtable, so
			// just retrieve them and we can look through them
			final Enumeration<WatchableList> iter = theParticipants.elements();
			while(iter.hasMoreElements())
			{
				final Object oj = iter.nextElement();
				if(oj instanceof WatchableList)
				{
					final WatchableList thisList = (WatchableList) oj;
					// check if this watchable found is visible

          list = thisList.getNearestTo(newDTG);

					Watchable wat = null;
          if(list.length > 0)
             wat = list[0];

					if(wat != null){
						if(wat.getVisible()){
							newHighlights.put(wat, thisList);
						}
					}
				}
			}
		}

		// we now have our lists, lets plot them
		// Get the graphics
    if(canvas == null)
      canvas = _theChart.getCanvas();

    final Graphics dest = canvas.getGraphicsTemp();

		// check we were able to get our destination plotting canvas
		// and drop out if we haven't - it's no surprise if we
		// can't get the temp graphics item --> it may be in a redraw
		if(dest == null)
		{
			return;
		}

    // over-ride the line thickness to ensure it's only 1 pixel wide
    if(dest instanceof Graphics2D)
    {
      Graphics2D g2 = (Graphics2D) dest;
      g2.setStroke(new BasicStroke(MARKER_THICKNESS));
    }

    // set the XOR painting mode
    dest.setXORMode(canvas.getBackgroundColor());
    final PlainProjection proj = _theChart.getCanvas().getProjection();

		// remove the old primary highlight
		if(!_firstStep)
		{
			// check that we had an old primary point - since we may
			// have been looking outside the time period for this track
			if(oldPrimary != null)
      {
				getCurrentPrimaryHighlighter().highlightIt(proj, dest, _theTote.getPrimary(), oldPrimary);
      }
		}

		// now step through our old highlights, hiding them
		final Iterator<Watchable> oldies = oldHighlights.keySet().iterator();

		// get rid of the old ones
		while(oldies.hasNext())
		{
			final Watchable oldWt = oldies.next();

			if(_firstStep)
			{
				// first step, nothing to delete!
			}
			else
			{
        // if the primary highlighter is symbols, we use them for both tracks
        if(plottingSymbols)
          getCurrentPrimaryHighlighter().highlightIt(proj, dest, oldHighlights.get(oldWt), oldWt);
        else
			    getCurrentSecondaryHighlighter().highlightIt(proj, dest, oldHighlights.get(oldWt), oldWt);
			}

		}

		// and now plot the new ones, if we have a valid primary point
		if(newPrimary != null)
    {
			getCurrentPrimaryHighlighter().highlightIt(proj, dest, _theTote.getPrimary(), newPrimary);

    }

		// now step through our new highlights, showing them
		final Iterator<Watchable> newies = newHighlights.keySet().iterator();

		// paint the new updates
		while(newies.hasNext())
		{
			final Watchable newWt = newies.next();

       // if the primary highlighter is symbols, we use them for both tracks
      if(plottingSymbols)
        getCurrentPrimaryHighlighter().highlightIt(proj, dest, newHighlights.get(newWt), newWt);
      else
  			getCurrentSecondaryHighlighter().highlightIt(proj, dest, newHighlights.get(newWt),newWt);
		}

		// restore the painting setup
    dest.setPaintMode();
    dest.dispose();


		// we know we're finished with the first step now anyway
		_firstStep = false;

    _lastDTG = newDTG;

		// now store the new highlights as the old highlights...
		oldHighlights = newHighlights;
		oldPrimary = newPrimary;

		// if we aren't doing a repaint
		if(!_inRepaint)
		{
			// force a repaint of the plot

			// grow the area covered by a shade,
      if(_areaCovered != null)
			  _areaCovered.grow(2, 2);

      // see if we are trying to plot in relative mode - in which
      // case we need a full repaint
      if(proj.getRelativePlot())
      {
        _theChart.update();
      }
      else
      {

        // and ask for an instant update - IGNORE the area covered command,
        // since it causes just a purple box to appear on the
        // screen in JDK1.3
       // was: _theChart.repaintNow(_areaCovered);
        _theChart.repaint();
      }
		}
    else
    {
      // everything has been handled OK if we are repainting
    }


  }

  /** return the hightlighter currently in use
   */
  public final PlotHighlighter getCurrentPrimaryHighlighter()
  {
    return _theTote.getCurrentHighlighter();
  }

  /** return the highlighter for the primary track
   */
  private PlotHighlighter getCurrentSecondaryHighlighter()
  {
    return _theTote.getDefaultHighlighter();
  }

/** draw a highlight around this point
 * @param proj screen projection we are using
 * @param dest canvas to plot onto
 * @param watch the thing we are highlighting
 */
  protected static final void highlightIt(final PlainProjection proj,
                             final Graphics dest,
                             final Watchable watch)
  {

  }

/** draw a highlight around this contact
 * @param proj the screen projection we are using
 * @param dest the canvas to plot onto
 * @param contact the contact to highlight
 */
  protected final void highlightContact(final PlainProjection proj,
																	final Graphics dest,
																	final ContactWrapper contact)
	{
		// set the highlight colour
		dest.setColor(Color.white);

		final WorldLocation start = contact.getStart();
		final WorldLocation end = contact.getEnd();

		// convert to screen coordinates
		final Point tl = new Point(proj.toScreen(start));
		final Point br = new Point(proj.toScreen(end));
		// get the width
		final int x = tl.x - _mySize;
		final int y = tl.y - _mySize;
		final int wid = (br.x - tl.x) + _mySize * 2;
		final int ht = (br.y - tl.y) + _mySize * 2;

		// represent this area as a rectangle
		final java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

		// keep track of the area covered
		if(_areaCovered == null)
			_areaCovered = thisR;
		else
			_areaCovered.add(thisR);

		// plot the rectangle
		dest.drawLine(tl.x+1, tl.y+1, br.x+1, br.y+1);

	}



/** whether we have an editor
 * @return yes, of course
 */
  public boolean hasEditor()
  {
    return true;
  }

/** get the editable information for this painter
 * @return editable details
 */
  public Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new TotePainterInfo(this);

    return _myEditor;
  }

  /////////////////////////////////////////////////////////////
  // nested class describing how to edit this class
  ////////////////////////////////////////////////////////////
/** the set of editable details for the painter
 */
  public static final class TotePainterInfo extends Editable.EditorType
  {

/** constructor for editable
 * @param data the object we are editing
 */
    public TotePainterInfo(final TotePainter data)
    {
      super(data, "Normal", "");
    }

/** the set of descriptions for this object
 * @return the properties
 */
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
// no properties for this type of cursor
//          prop("Color", "Color to paint highlight"),
//          prop("Size", "size to paint highlight (pixels"),
        };
        return res;
      }
      catch(Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        return super.getPropertyDescriptors();
      }

    }
  }

/** our string representation
 * @return the name
 */
	public String toString()
	{
		return "Normal";
	}

/** whether we are in a repaint event
 * @return whether we are currently repainting
 */
	public final boolean isRepainting()
	{
		return _inRepaint;
	}

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE  = "UNIT";
    public testMe(final String val)
    {
      super(val);
    }
    public final void testMyParams()
    {
      Editable ed = new TotePainter(null,null,null);
      Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}

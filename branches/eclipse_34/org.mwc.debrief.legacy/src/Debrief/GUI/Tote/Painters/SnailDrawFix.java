package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawFix.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: SnailDrawFix.java,v $
// Revision 1.4  2005/12/13 09:04:25  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2005/01/28 10:52:55  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.2  2004/11/25 10:24:02  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:20  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.8  2003-07-04 10:59:22+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.7  2003-07-01 14:56:55+01  ian_mayo
// Don't bother getting the fix to paint itself, since we plot it from the TrackPainter
//
// Revision 1.6  2003-03-19 15:38:00+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2002-12-16 15:12:46+00  ian_mayo
// Improved vector stretch (larger aswell as smaller)
//
// Revision 1.4  2002-10-28 09:04:36+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.3  2002-07-23 08:49:21+01  ian_mayo
// Set the correct line width
//
// Revision 1.2  2002-05-28 12:28:01+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:59+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-01-17 20:21:45+00  administrator
// Reflect switch to Duration object
//
// Revision 1.2  2001-10-03 16:07:07+01  administrator
// Store editable name as parameter, so it can be set by either snail mode or relative mode
//
// Revision 1.1  2001-08-29 19:18:40+01  administrator
// Contacts removed
//
// Revision 1.0  2001-07-17 08:41:39+01  administrator
// Initial revision
//
// Revision 1.5  2001-01-24 12:13:06+00  novatech
// reflect switch to minutes
//
// Revision 1.4  2001-01-24 11:35:52+00  novatech
// recognise optimised toScreen handling which reduces object creation
//
// Revision 1.3  2001-01-22 12:30:04+00  novatech
// added JUnit testing code
//
// Revision 1.2  2001-01-17 09:44:56+00  novatech
// support symbol plotter
//
// Revision 1.1  2001-01-03 13:40:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:36  ianmayo
// initial import of files
//
// Revision 1.18  2000-10-17 16:07:56+01  ian_mayo
// provide convenience functions for whether user is able to set points to fade with time
//
// Revision 1.17  2000-10-10 14:08:42+01  ian_mayo
// reflect name change
//
// Revision 1.16  2000-10-09 13:37:44+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.15  2000-09-27 14:46:59+01  ian_mayo
// name changes
//
// Revision 1.14  2000-09-26 09:50:57+01  ian_mayo
// use Projection algorithms in creation of "Stalk" for fix
//
// Revision 1.13  2000-09-22 12:55:13+01  ian_mayo
// use the FixWrapper code to plot itself
//
// Revision 1.12  2000-09-22 11:44:14+01  ian_mayo
// insert header comments
//

import java.awt.*;
import java.beans.PropertyDescriptor;

import Debrief.Tools.Tote.*;
import Debrief.Wrappers.*;
import MWC.GUI.Editable;
import MWC.GUI.Properties.*;
import MWC.GenericData.*;


public final class SnailDrawFix implements SnailPainter.drawHighLight, Editable
{

	/** keep a copy of the track plotter we are using
	 */
	private final SnailDrawTrack _trackPlotter = new SnailDrawTrack();

	/** keep a copy of the requested point size
	 */
  private int _pointSize;

	/** do we draw in the track/vessel name?
	 */
  private boolean _plotName;

	/** the 'stretch' factor to put on the speed vector (factor to apply to the speed vector, in pixels)
	 */
  private double _vectorStretch;

  /** our editor
   */
  transient private Editable.EditorType _myEditor = null;

  /** the name we display when shown in an editor
   *  (which may initially be Snail or Relative)
   */
  private final String _myName;

  /*******************************************************
   * constructor
   ******************************************************/
  public SnailDrawFix(final String name)
  {
    _myName = name;
  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public final java.awt.Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
																	 final java.awt.Graphics dest,
																	 final WatchableList list,
																	 final Watchable watch,
																	 final SnailPainter parent,
																	 final HiResDate dtg,
                                   final java.awt.Color backColor)
	{
    Rectangle thisR = null;

    dest.setXORMode(backColor);

    // get a pointer to the fix
		final FixWrapper fix = (FixWrapper)watch;

		// get the colour of the track
		final Color col = fix.getColor();
		dest.setColor(col);

    // produce the centre point
    final Point p = new Point(proj.toScreen(fix.getLocation()));

    // see if we are in symbol plotting mode
    final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter thisHighlighter = parent.getCurrentPrimaryHighlighter ();
    if(thisHighlighter instanceof Debrief.GUI.Tote.Painters.Highlighters.SymbolHighlighter)
    {
      // just plot away!
      thisHighlighter.highlightIt(proj, dest, list, watch);

      // work out the area covered
      final WorldArea wa = watch.getBounds();
      final WorldLocation tl = wa.getTopLeft();
      final WorldLocation br = wa.getBottomRight();
      final Point pTL = new Point(proj.toScreen(tl));
      final Point pBR = new Point(proj.toScreen(br));
      final Rectangle thisArea = new java.awt.Rectangle(pTL);
      thisArea.add(pBR);
      if(thisR == null)
        thisR = thisArea;
      else
        thisR.add(thisArea);

    }
    else
    {
      // plot the pointy vector thingy

      // get the current area of the watchable
      final WorldArea wa = watch.getBounds();
      // convert to screen coordinates
      final Point tl = new Point(proj.toScreen(wa.getTopLeft()));
      final Point br = new Point(proj.toScreen(wa.getBottomRight()));

      final int mySize = _pointSize;

      // get the width
      final int x = tl.x - mySize;
      final int y = tl.y - mySize;
      final int wid = (br.x - tl.x) + mySize * 2;
      final int ht = (br.y - tl.y) + mySize * 2;

      // represent this area as a rectangle
      thisR = new Rectangle(x, y, wid, ht);

      // plot the rectangle anyway
      dest.drawOval(x , y, wid, ht);

      // get the fix to draw itself

      // create our own canvas object (don't bother - do it all from the Track, so we know
      // the correct size of the resulting object
//      final CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

      // and do the paint
   //   fix.paintMe(cad);

      // and now plot the vector
      final double crse = watch.getCourse();
      final double spd = watch.getSpeed();

      //
      final int dx = (int)(Math.sin(crse) * mySize * spd * _vectorStretch);
      final int dy = (int)(Math.cos(crse) * mySize * spd * _vectorStretch);


      // produce the end of the stick (just to establish the length in data units)
      final Point p2 = new Point(p.x + dx, p.y - dy);

      // how long is the stalk in data units?
      final WorldLocation w3 = proj.toWorld(p2);
      final double len = w3.rangeFrom(fix.getLocation());

      // now sort out the real end of this stalk
      final WorldLocation stalkEnd = fix.getLocation().add(new WorldVector(crse, len, 0));
      // and get this in screen coordinates
      final Point pStalkEnd = proj.toScreen(stalkEnd);

      // and plot the stalk itself
      dest.drawLine(p.x, p.y, pStalkEnd.x, pStalkEnd.y);

      // extend the area covered to include the stick
      thisR.add(p2);

    }

		// draw the trailing dots
		final java.awt.Rectangle dotsArea = _trackPlotter.drawMe(proj,
																											 dest,
																											 watch,
																											 parent,
																											 dtg,
                                                       backColor);

		// extend the rectangle, if necesary
		if(dotsArea != null)
			thisR.add(dotsArea);

		// plot the track name
		if(_plotName)
		{
			final String msg = fix.getTrackWrapper().getName();

			// shift the centre point across a bit
			p.translate(5, 0);

			// HACK: TRY TO PAINT THIS IN
			dest.setXORMode(Color.black);

			// and draw the text
			dest.drawString(msg, p.x, p.y);

			// somehow we need to include this extended area
			final FontMetrics fm = dest.getFontMetrics();

			final int sWid = fm.stringWidth(msg);

			// shift from the start of the string
			p.translate(sWid, 0);

			// and add to the limits rectangle
			thisR.add(p);
		}

    // set the width
//    if(dest instanceof CanvasType)
//    {
//      CanvasType ct = (CanvasType)dest;
//      ct.setLineWidth(1);
//    }
//    if(dest instanceof Graphics2D)
//    {
//      Graphics2D g2 = (Graphics2D)dest;
//      BasicStroke bs = new BasicStroke(1);
//    }

		return thisR;
	}

	public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if(wt instanceof Debrief.Wrappers.FixWrapper)
		{
			res = true;
		}
		return res;
	}

  protected static final void highlightContact(final MWC.Algorithms.PlainProjection proj,
																	final Graphics dest,
																	final ContactWrapper contact,
																	final int mySize,
																	Rectangle areaCovered)
	{
		// set the highlight colour
		dest.setColor(Color.white);

		final WorldLocation start = contact.getStart();
		final WorldLocation end = contact.getEnd();

		// convert to screen coordinates
		final Point tl = new Point(proj.toScreen(start));
		final Point br = new Point(proj.toScreen(end));
		// get the width
		final int x = tl.x - mySize;
		final int y = tl.y - mySize;
		final int wid = (br.x - tl.x) + mySize * 2;
		final int ht = (br.y - tl.y) + mySize * 2;

		// represent this area as a rectangle
		final java.awt.Rectangle thisR = new Rectangle(x, y, wid, ht);

		// keep track of the area covered
		if(areaCovered == null)
			areaCovered = thisR;
		else
			areaCovered.add(thisR);

		// plot the rectangle
		dest.drawLine(tl.x+1, tl.y+1, br.x+1, br.y+1);

	}

  public final String getName()
  {
    return _myName;
  }

  public final String toString()
  {
    return getName();
  }

  public final boolean hasEditor()
  {
    return true;
  }

  public final Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new SnailFixPainterInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////////////
  // accessors for editable parameters
  /////////////////////////////////////////////////////////


  public final void setLinkPositions(final boolean val)
  {
    _trackPlotter.setJoinPositions(val);
  }

  public final boolean getLinkPositions()
  {
    return  _trackPlotter.getJoinPositions();
  }

  public final void setFadePoints(final boolean val)
  {
    _trackPlotter.setFadePoints(val);
  }

  public final boolean getFadePoints()
  {
    return _trackPlotter.getFadePoints();
  }

  /** point size of symbols (pixels)
   */
  public final BoundedInteger getPointSize()
  {
    return new BoundedInteger(_trackPlotter.getPointSize(),
															1,
															20);
  }

  /** length of trail to plot
   */
  public final Duration getTrailLength()
  {
    return new Duration(_trackPlotter.getTrailLength().longValue(), Duration.MICROSECONDS);
  }

  /** size of points to draw (pixels)
   */
  public final void setPointSize(final BoundedInteger val)
  {

    _trackPlotter.setPointSize(val.getCurrent());
		_pointSize = val.getCurrent();
  }

  /** length of trail to draw
   */
  public final void setTrailLength(final Duration len)
  {
    _trackPlotter.setTrailLength(new Long((long)len.getValueIn(Duration.MICROSECONDS)));
  }

	/** whether to plot in the name of the vessel
	 */
	public final boolean getPlotTrackName()
	{
		return _plotName;
	}

	/** whether to plot in the name of the vessel
	 */
	public final void setPlotTrackName(final boolean val)
	{
		_plotName = val;
	}

	/** how much to stretch the vector
	 */
	public final void setVectorStretch(final double val)
	{
		_vectorStretch = val;
	}

	/** how much to stretch the vector
	 */
	public final double getVectorStretch()
	{
		return _vectorStretch;
	}


  //////////////////////////////////////////////////////////
  // nested editable class
  /////////////////////////////////////////////////////////

  public static final class SnailFixPainterInfo extends Editable.EditorType
  {

    public SnailFixPainterInfo(final SnailDrawFix data)
    {
      super(data, "Snail Painter", "");
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
          prop("LinkPositions", "whether to join the points in the trail"),
          prop("PlotTrackName", "whether to plot the name of the track"),
          prop("FadePoints", "whether the trails should fade to black"),
          prop("PointSize", "the size of the points in the trail"),
          prop("TrailLength", "the length of trail to draw"),
          prop("VectorStretch", "how far to stretch the speed vector (pixels per knot)"),
        };

        res[5].setPropertyEditorClass(FractionPropertyEditor.class);

        return res;
      }
      catch(Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
        return super.getPropertyDescriptors();
      }

    }

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
      Editable ed = new SnailDrawFix("testing");
      Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}


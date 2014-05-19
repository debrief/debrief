package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawBuoyPattern.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SnailDrawBuoyPattern.java,v $
// Revision 1.3  2005/12/13 09:04:25  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:02  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:19  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2002-12-16 15:11:43+00  ian_mayo
// lots of tidying, as directed by IntelliJ Idea
//
// Revision 1.2  2002-05-28 12:28:02+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:58+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:40+01  administrator
// Initial revision
//
// Revision 1.1  2001-02-27 11:44:06+00  novatech
// Initial revision
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

import java.awt.Point;
import java.awt.Rectangle;

import Debrief.Wrappers.BuoyPatternWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;


/** class to handle drawing a buoy pattern when we're in snail mode
 *
 */
public final class SnailDrawBuoyPattern implements SnailPainter.drawHighLight, Editable
{

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
		final BuoyPatternWrapper.BuoyPatternAdaptor adaptor = (BuoyPatternWrapper.BuoyPatternAdaptor)watch;

    final BuoyPatternWrapper pattern = adaptor.getPattern();

    // get the current area of the watchable
    final WorldArea wa = watch.getBounds();
    // convert to screen coordinates
    final Point tl = new Point(proj.toScreen(wa.getTopLeft()));
    final Point br = new Point(proj.toScreen(wa.getBottomRight()));

    // create our own canvas object
    final CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

    // and do the paint
    pattern.paint(cad);

    // extend the area covered to include the stick

    thisR =  new Rectangle(tl);
    thisR.add(br);

		return thisR;
	}

	public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if(wt instanceof Debrief.Wrappers.BuoyPatternWrapper.BuoyPatternAdaptor)
		{
			res = true;
		}
		return res;
	}



  public final String getName()
  {
    return "Snail Buoypattern Plotter";
  }

  public final String toString()
  {
    return getName();
  }

  public final boolean hasEditor()
  {
    return false;
  }

  public final Editable.EditorType getInfo()
  {
    return null;
  }

  //////////////////////////////////////////////////////////
  // accessors for editable parameters
  /////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////
  // nested editable class
  /////////////////////////////////////////////////////////


}


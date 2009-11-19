package Debrief.GUI.Tote.Painters;

// Copyright MWC 2000, Debrief 3 Project
// $RCSfile: RelativePainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: RelativePainter.java,v $
// Revision 1.3  2005/12/13 09:04:24  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:01  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:18  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:24+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:38:03+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:57+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:18+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:50+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:56+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-10-08 20:18:29+01  administrator
// Initial revision
//
// Revision 1.3  2001-10-01 12:49:48+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-24 16:36:29+01  administrator
// Handle stepping before tracks assigned
//
// Revision 1.1  2001-08-14 14:07:09+01  administrator
// Add the new SnailDrawContact, and extend getWatchables to recognise any SensorWrapper's which are in a Track
//
// Revision 1.0  2001-07-17 08:41:39+01  administrator
// Initial revision
//
// Revision 1.8  2001-04-08 10:45:57+01  novatech
// Correct problem where LabelWrapper with times are stored as Watchables and Non-Watchables (since we did not recognise their type)
//
// Revision 1.7  2001-02-01 09:29:42+00  novatech
// implement correct handling of null time (-1, not 0 as before) and reflect fact that we no longer create/re-create our oldWatchables list, we empty and fill it
//
// Revision 1.6  2001-01-22 12:30:04+00  novatech
// added JUnit testing code
//
// Revision 1.5  2001-01-18 13:15:07+00  novatech
// create buoy plotter for snail mode
//
// Revision 1.4  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.3  2001-01-15 11:21:28+00  novatech
// store the old points in a hashmap instead of a vector, so that the track can be stored aswell as the fix
//
// Revision 1.2  2001-01-09 10:27:25+00  novatech
// use WatchableList as well as  TrackWrapper
//
// Revision 1.1  2001-01-03 13:40:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:49  ianmayo
// initial import of files
//
// Revision 1.26  2000-11-24 10:53:23+00  ian_mayo
// tidying up
//
// Revision 1.25  2000-11-17 09:15:32+00  ian_mayo
// allow code to drop out if we can't create our graphics object (ie before panels are setVisible)
//
// Revision 1.24  2000-11-08 11:48:25+00  ian_mayo
// reflect change in status of TrackWrapper to Layer
//
// Revision 1.23  2000-11-02 16:45:48+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.22  2000-10-17 16:07:09+01  ian_mayo
// move HighlightPlotting to before vector plotting, so that vectors are visible.  Play around with when we plot non-watchables, so that scale is always plotted
//
// Revision 1.21  2000-10-09 13:37:47+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.20  2000-10-03 14:17:17+01  ian_mayo
// draw primary with specified highlighter
//
// Revision 1.19  2000-09-27 14:47:40+01  ian_mayo
// name changes
//
// Revision 1.18  2000-09-27 14:31:45+01  ian_mayo
// put relativePlotting into correct place
//
// Revision 1.17  2000-09-26 09:50:37+01  ian_mayo
// support for relative plotting
//
// Revision 1.16  2000-09-22 11:44:51+01  ian_mayo
// add AnnotationPlotter, & improve method for detecting if a plottable should be added to the Watchable list or not
//
// Revision 1.15  2000-09-18 09:14:37+01  ian_mayo
// GUI name changes
//
// Revision 1.14  2000-08-30 14:49:05+01  ian_mayo
// rx background colour, instead of retrieving it yourself
//
// Revision 1.13  2000-08-14 15:50:05+01  ian_mayo
// GUI name changes
//
// Revision 1.12  2000-08-11 08:41:00+01  ian_mayo
// tidy beaninfo
//
// Revision 1.11  2000-07-07 09:58:59+01  ian_mayo
// Tidy up name of panel
//
// Revision 1.10  2000-06-19 15:06:19+01  ian_mayo
// newlines tidied up
//
// Revision 1.9  2000-06-06 12:43:47+01  ian_mayo
// replot full diagram, not just small areas (to overcome problem in JDK1.3)
//
// Revision 1.8  2000-04-03 10:19:21+01  ian_mayo
// switch to returning editable belonging to Painter, not us
//
// Revision 1.7  2000-03-27 14:44:01+01  ian_mayo
// redraw chart after we have been changed
//
// Revision 1.6  2000-03-17 13:38:44+00  ian_mayo
// Tidying up
//
// Revision 1.5  2000-03-14 09:52:39+00  ian_mayo
// allow configurable "leg" for vector plotting
//
// Revision 1.4  2000-03-09 11:26:31+00  ian_mayo
// add method/accessor to allow user to request vessel name on track
//
// Revision 1.3  2000-03-08 16:23:35+00  ian_mayo
// represent symbol shape size as bounded integer
//
// Revision 1.2  2000-03-08 14:26:10+00  ian_mayo
// further through implementation
//
// Revision 1.1  2000-03-07 13:44:08+00  ian_mayo
// Initial revision
//



import Debrief.GUI.Tote.AnalysisTote;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

public final class RelativePainter extends SnailPainter
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  ///////////////////////////////////
  // constructorsna
  //////////////////////////////////
  public RelativePainter(final PlainChart theChart,
                     final Layers theData,
                     final AnalysisTote theTote)
  {
    super(theChart, theData, theTote, "Relative");
  }



  //
  public final void steppingModeChanged(final boolean on)
  {
    // inform the parent
    super.steppingModeChanged(on);

    // are we now on?
    if(on)
    {
      // we have been switched on, set to relative
      super._theChart.getCanvas().getProjection().setRelativePlot(true);
    }
    else
    {
      // no, we have been switched off, reset the relative projection
      super._theChart.getCanvas().getProjection().setRelativePlot(false);
    }

  }

  public final String toString()
  {
    return "Relative";
  }


  /** NON-STANDARD implementation, we are returning the editor for our
   * snail plotter object, not ourself
   */
//  public Editable.EditorType getInfo()
//  {
//    return new RelativePainterInfo(this);
//  }


  /////////////////////////////////////////
  // accessors for the beaninfo
  ////////////////////////////////////////
  //////////////////////////////////////////////////////////
  // accessors for editable parameters
  /////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////
  // nested class describing how to edit this class
  ////////////////////////////////////////////////////////////

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
      Editable ed = new SnailPainter(null,null,null);
      Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}

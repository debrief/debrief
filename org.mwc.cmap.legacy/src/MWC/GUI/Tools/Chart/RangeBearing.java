/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Tools.Chart;

// Copyright MWC 1999
// $RCSfile: RangeBearing.java,v $
// $Author: Ian.Mayo $
// $Log: RangeBearing.java,v $
// Revision 1.2  2004/05/25 15:43:52  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:43  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:59+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:43+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:57+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:56:37+01  novatech
// give the formatting responsibility to the status bar
//
// Revision 1.1  2001-01-03 13:41:51+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:22  ianmayo
// initial version
//
// Revision 1.3  2000-08-07 12:21:26+01  ian_mayo
// tidy icon filename
//
// Revision 1.2  2000-03-14 09:54:52+00  ian_mayo
// use icons for these tools
//
// Revision 1.1  1999-10-12 15:36:21+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-17 08:06:55+01  administrator
// make serializable
//
// Revision 1.1  1999-07-27 10:59:45+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-27 09:27:03+01  administrator
// tidying up use of tools
//
// Revision 1.1  1999-07-08 13:09:17+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:15+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//

import java.awt.Point;

import MWC.GUI.PlainChart;
import MWC.GUI.Rubberband;
import MWC.GUI.StatusBar;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainDragTool;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class RangeBearing extends PlainDragTool implements java.io.Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  WorldLocation _startLocation;
  StatusBar _theBar;

  protected Rubberband _myRubber=new MWC.GUI.RubberBanding.RubberbandLine();

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public RangeBearing(final PlainChart theChart,
                      final ToolParent theParent,
                      final StatusBar theBar){
    super(theChart, theParent, "Range Brg", "images/rng_brg.gif");
    _theBar = theBar;

  }


  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  public Rubberband getRubberband(){
    return _myRubber;
  }


  public void startMotion(){
    // pop up the window?
  }


  public void dragging(final WorldLocation theLocation, final Point thePoint)
  {
    // right, now sort out the range and bearing back to
    // the origin
    final WorldVector wv = theLocation.subtract(_theStart);

    // get the data
    final double rng = wv.getRange();
    final double brg = wv.getBearing();


    // we don't format the results anymore, since the status bar knows how to
    // plot range and bearing information
    _theBar.setRngBearing(rng, brg);

  }

  public String getName(){
    return "Zoom tool";
  }

  public Action getData(){
    return null;
  }

  public void showResults(final String val)
  {
    _theBar.setText(val);
  }

}

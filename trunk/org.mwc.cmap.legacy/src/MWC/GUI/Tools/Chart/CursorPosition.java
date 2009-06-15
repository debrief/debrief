package MWC.GUI.Tools.Chart;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CursorPosition.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CursorPosition.java,v $
// Revision 1.2  2004/05/25 15:43:41  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:40+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:55+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:12  ianmayo
// initial version
//
// Revision 1.2  1999-11-18 11:09:04+00  ian_mayo
// abstract, to allow for Swing-specific behaviour
//
// Revision 1.1  1999-10-12 15:36:18+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-17 08:14:08+01  administrator
// change to way layer data is passed
//
// Revision 1.1  1999-07-27 10:59:47+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:49+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-12 08:09:28+01  administrator
// Initial revision
//

import java.io.Serializable;

import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldLocation;

abstract public class CursorPosition implements PlainChart.ChartCursorMovedListener, Serializable
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public CursorPosition(PlainChart theChart){
    theChart.addCursorMovedListener(this);
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////


  public void cursorMoved(WorldLocation thePos, boolean dragging, Layers theData)
  {
    
    String res = MWC.Utilities.TextFormatting.BriefFormatLocation.toString(thePos);
    setText(res);
  }
	
	abstract public void setText(String theVal);
}

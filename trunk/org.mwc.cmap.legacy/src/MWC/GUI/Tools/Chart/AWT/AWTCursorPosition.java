package MWC.GUI.Tools.Chart.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTCursorPosition.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTCursorPosition.java,v $
// Revision 1.2  2004/05/25 15:44:00  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:44  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
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
// Revision 1.1.1.1  2000/12/12 21:51:43  ianmayo
// initial version
//
// Revision 1.2  1999-11-25 13:32:42+00  ian_mayo
// changed names
//

import MWC.GUI.*;
import MWC.GUI.Tools.Chart.*;

public class AWTCursorPosition extends CursorPosition
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected java.awt.Label _myOutput;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public AWTCursorPosition(PlainChart theChart,
													 java.awt.Component theLabel){
		super(theChart);
		_myOutput = (java.awt.Label) theLabel;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
	public void setText(String val)
	{
		_myOutput.setText(val);
	}
}

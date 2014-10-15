/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Tools.Chart.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingCursorPosition.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingCursorPosition.java,v $
// Revision 1.2  2004/05/25 15:44:02  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:44  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:39+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:54+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:43  ianmayo
// initial version
//
// Revision 1.2  1999-11-23 11:05:02+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 17:22:26+00  ian_mayo
// Initial revision
//

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Chart.CursorPosition;

public class SwingCursorPosition extends CursorPosition
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  protected javax.swing.JLabel _myOutput;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public SwingCursorPosition(final PlainChart theChart,
													 final java.awt.Component theLabel){
		super(theChart);
		_myOutput = (javax.swing.JLabel) theLabel;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
	public void setText(final String val)
	{
		_myOutput.setText(val);
	}
}

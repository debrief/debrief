package MWC.GUI.RubberBanding;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: RubberbandDrag.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: RubberbandDrag.java,v $
// Revision 1.2  2004/05/25 15:36:50  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:32  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:51+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:10+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:17+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:28+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:48:57  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:04:30+01  administrator
// Initial revision
//

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import MWC.GUI.Rubberband;


public class RubberbandDrag extends Rubberband implements Serializable
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
	public RubberbandDrag() {
	}
  public RubberbandDrag(Component component) {
      super(component);
  }  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public void drawLast(Graphics graphics) {

      graphics.drawRect(lastPt.x-3, lastPt.y-3, 7, 7);
  }
  public void drawNext(Graphics graphics) {

      graphics.drawRect(stretchedPt.x-3, stretchedPt.y-3, 7, 7);
  }
}

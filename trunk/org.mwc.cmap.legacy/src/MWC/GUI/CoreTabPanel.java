// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CoreTabPanel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CoreTabPanel.java,v $
// Revision 1.2  2004/05/25 15:45:25  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:02  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:24+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:05+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:45  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:49:49+01  ian_mayo
// Initial revision
//
package MWC.GUI;

import java.awt.*;

public interface CoreTabPanel 
{
	public int addTabPanel(String sLabel, boolean bEnabled, Component panel);
}

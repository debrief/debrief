// Copyright MWC 1999
// $RCSfile: CloseSession.java,v $
// $Author: Ian.Mayo $
// $Log: CloseSession.java,v $
// Revision 1.1.1.2  2003/07/21 14:48:27  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:10+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:24+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:57+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:54+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-08 17:12:03+01  administrator
// Rename button
//
// Revision 1.0  2001-07-17 08:41:18+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:29+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:16  ianmayo
// initial import of files
//
// Revision 1.4  2000-08-07 14:05:12+01  ian_mayo
// correct image-naming
//
// Revision 1.3  2000-08-07 12:24:39+01  ian_mayo
// tidy icon filename
//
// Revision 1.2  2000-03-14 09:49:14+00  ian_mayo
// assign icon names to tools
//
// Revision 1.1  1999-10-12 15:34:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:14+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:05+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:26+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-02-01 14:25:07+00  sm11td
// Initial revision
//

package Debrief.Tools.Operations;

import MWC.GUI.Tools.*;
import Debrief.GUI.Frames.*;

public final class CloseSession extends PlainTool
{

  private final Application _theApplication;

  /** produce the Command item - not necessary, since this is not
   *  undoable
   */
  public final Action getData(){
    return null;
  }

  public CloseSession(Application theApplication){
    super(theApplication, "Close Plot", "images/close.gif");
    _theApplication = theApplication;
  }


  public final void execute()
  {
    _theApplication.closeSession(_theApplication.getCurrentSession());
  }
}

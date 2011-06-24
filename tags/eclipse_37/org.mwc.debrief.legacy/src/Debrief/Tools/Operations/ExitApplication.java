// Copyright MWC 1999
// $RCSfile: ExitApplication.java,v $
// $Author: Ian.Mayo $
// $Log: ExitApplication.java,v $
// Revision 1.1.1.2  2003/07/21 14:48:29  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:02+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:26+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:55+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:18+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:30+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:16  ianmayo
// initial import of files
//
// Revision 1.3  2000-08-07 12:23:21+01  ian_mayo
// tidy icon filename
//
// Revision 1.2  2000-03-14 09:48:52+00  ian_mayo
// assign icon filename
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
// Revision 1.1  1999-02-01 16:07:45+00  sm11td
// Initial revision
//

package Debrief.Tools.Operations;





import MWC.GUI.Tools.*;
import Debrief.GUI.Frames.*;

public final class ExitApplication extends PlainTool
{
  
  private final Application _theApplication;
  
  /** produce the Command item - not necessary, since this is not
   *  undoable
   */
  public final Action getData(){
    return null;
  }

  public ExitApplication(Application theApplication){
    super(theApplication, "Exit", "images/exit.gif");
    _theApplication = theApplication;
  }
  

  public final void execute()
  {
    _theApplication.exit();
  }
}

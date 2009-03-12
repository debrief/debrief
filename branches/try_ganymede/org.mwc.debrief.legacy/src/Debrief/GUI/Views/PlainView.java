// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainView.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainView.java,v $
// Revision 1.2  2005/12/13 09:04:31  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:33  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-05-14 16:09:51+01  ian_mayo
// make getParent public
//
// Revision 1.3  2003-03-19 15:38:04+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:05+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:50+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:36+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:51:57+01  novatech
// provide accessor to get the ToolParent
//
// Revision 1.1  2001-01-03 13:40:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:32  ianmayo
// initial import of files
//
// Revision 1.2  2000-04-19 11:26:49+01  ian_mayo
// add Close signature (to clear local storage)
//
// Revision 1.1  1999-10-12 15:34:16+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-27 09:27:47+01  administrator
// general improvements
//
// Revision 1.1  1999-07-07 11:10:18+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:08+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:30+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:33:14+00  sm11td
// Initial revision
//


package Debrief.GUI.Views;

import MWC.GUI.ToolParent;

/** blank class which acts as parent to the different types
 * of view which may be displayed/constructed within the application
 */
abstract public class PlainView {

  final ToolParent _theParent;
  private String _name="blank";

  /** list of panes contained in this view*/

  public PlainView(final String theName, final ToolParent theParent){
    _name = theName;
    _theParent = theParent;
  }

  public final String getName(){
    return _name;
  }

  public final ToolParent getParent()
  {
    return _theParent;
  }

  abstract public void update();
  abstract public void rescale();
	abstract public void close();
}



















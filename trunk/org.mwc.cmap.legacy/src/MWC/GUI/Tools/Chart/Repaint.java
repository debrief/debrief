// Copyright MWC 1999
// $RCSfile: Repaint.java,v $
// $Author: Ian.Mayo $
// $Log: Repaint.java,v $
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
// Revision 1.1  2001-01-03 13:41:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:22  ianmayo
// initial version
//
// Revision 1.4  2000-08-07 12:21:51+01  ian_mayo
// tidy icon filename
//
// Revision 1.3  2000-03-14 09:54:48+00  ian_mayo
// use icons for these tools
//
// Revision 1.2  1999-11-23 10:38:37+00  ian_mayo
// shortened button label
//
// Revision 1.1  1999-10-12 15:36:22+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:34+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:59:46+01  administrator
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

package MWC.GUI.Tools.Chart;


import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;


public class Repaint extends PlainTool 
{

  private PlainChart _theChart;
  
  /** produce the Command item - not necessary, since this is not
   *  undoable
   */
  public Action getData(){
    return null;
  }

  public Repaint(ToolParent theParent,
                 PlainChart theChart)
  {
    super(theParent, "Redraw", "images/repaint.gif");
    _theChart = theChart;
  }
  

  public void execute()
  {
    _theChart.update();
  }
}

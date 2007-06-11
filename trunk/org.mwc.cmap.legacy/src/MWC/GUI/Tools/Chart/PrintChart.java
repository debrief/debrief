// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PrintChart.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PrintChart.java,v $
// Revision 1.2  2004/05/25 15:43:51  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:43  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:10+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:59+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:42+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:57+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:51+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:22  ianmayo
// initial version
//
// Revision 1.2  2000-11-02 15:13:36+00  ian_mayo
// remove printable line, to make jdk 1.1 compliant
//
// Revision 1.1  2000-10-10 14:09:49+01  ian_mayo
// Initial revision
//


package MWC.GUI.Tools.Chart;



import MWC.GUI.*;
import MWC.GUI.Tools.*;
import MWC.GenericData.*;

/** tool to instruct a particular chart to do a resize to fit all
 * of the current data
 */
public class PrintChart extends PlainTool {

  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////

  /** keep a reference to the chart which we are acting upon*/
  private PlainChart _theChart;

  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////

  /** constructor, stores information ready for when the button
   * finally gets pressed
   * @param theApp the parent application, so we can set cursors
   * @param theChart the chart we are to resize
   */
  public PrintChart(ToolParent theParent,PlainChart theChart){
    super(theParent, "Print Chart","images/fit_to_win.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
  }

  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////
  public Action getData()
  {
    // get the current data area
    java.awt.print.PrinterJob jp = java.awt.print.PrinterJob.getPrinterJob();
    jp.setJobName("Debrief Plot");
    if(jp.printDialog())
    {
      try
      {
        jp.print();
      }
      catch(java.awt.print.PrinterException e)
      {

      }
    }
    return null;
  }


}

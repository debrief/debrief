// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainMotionTool.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainMotionTool.java,v $
// Revision 1.2  2004/05/25 15:43:37  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:41  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:21+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:57+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:48+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:00+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:58+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:09  ianmayo
// initial version
//
// Revision 1.3  2000-03-14 09:55:14+00  ian_mayo
// process icon names
//
// Revision 1.2  1999-11-25 13:34:07+00  ian_mayo
// minor reformatting
//
// Revision 1.1  1999-10-12 15:36:26+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:33+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:43+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:03+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-01 16:49:22+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.1  1999-01-31 13:33:09+00  sm11td
// Initial revision
//

package MWC.GUI.Tools;

import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldLocation;

/** abstract class for tools which 'watch' the canvas, 
 * normally trapping movements
 */
abstract public class PlainMotionTool extends 
  PlainTool implements PlainChart.ChartCursorMovedListener{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  
  /** 
   * keep a reference to the chart we are currently 'watching'
   * */
  protected PlainChart _theChart;    
  
  protected WorldLocation _theStart;
  protected WorldLocation _theEnd;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  
  public PlainMotionTool(PlainChart theChart,
                         ToolParent theParent,
                         String theLabel,
                         String theImage){ 
    
    super(theParent, theLabel, theImage);
    
    // take copy of the chart we are to listen to    
    _theChart = theChart;
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////
  
  public PlainChart getChart(){
    return _theChart;
  }
  
  public void execute(){                       
    // add ourselves as the listener
    _theChart.addCursorMovedListener(this);
    
    // start the operation
    startMotion();
  }                        
  
  
  public void doExecute(Action theAction){
    // start busy
    setBusy(true);
    
    // do the action
    if(theAction != null)
      theAction.execute();
		
    // end busy
    setBusy(false);

  }
  
  abstract void startMotion();
}

/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: FitToWin.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: FitToWin.java,v $
// Revision 1.4  2005/09/16 10:00:56  Ian.Mayo
// Insert missing chart update
//
// Revision 1.3  2005/06/28 15:04:01  Ian.Mayo
// Minor tidying (make FitToWin action static)
//
// Revision 1.2  2004/05/25 15:43:44  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:14+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:26:00+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:41+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:56+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:16  ianmayo
// initial version
//
// Revision 1.4  2000-08-07 12:21:46+01  ian_mayo
// tidy icon filename
//
// Revision 1.3  2000-03-14 09:54:49+00  ian_mayo
// use icons for these tools
//
// Revision 1.2  1999-12-03 14:33:22+00  ian_mayo
// tidied up
//
// Revision 1.1  1999-10-12 15:36:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:59:45+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:48+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:14+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:05+01  sm11td
// Initial revision
//
// Revision 1.5  1999-06-01 16:49:22+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.4  1999-02-04 08:02:25+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.3  1999-02-01 16:08:49+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.2  1999-02-01 14:25:02+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:08+00  sm11td
// Initial revision
//

package MWC.GUI.Tools.Chart;



import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;

/** tool to instruct a particular chart to do a resize to fit all
 * of the current data
 */
public class FitToWin extends PlainTool {
  
  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////  
  
  /** keep a reference to the chart which we are acting upon*/
  private final PlainChart _theChart;  
  
  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  
  /** constructor, stores information ready for when the button
   * finally gets pressed
   * @param theParent the parent application, so we can set cursors
   * @param theChart the chart we are to resize
   */
  public FitToWin(final ToolParent theParent, 
                  final PlainChart theChart){      
    super(theParent, "fit to win","images/fit_to_win.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
  }
  
  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////  
  public Action getData()
  {
    // get the current data area
    final WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();
    return new FitToWinAction(_theChart, oldArea);
  }

  
  ////////////////////////////////////////////////////////////////////
  // store action information
  public static class FitToWinAction implements Action{
    private final PlainChart _theChart;  
    private final WorldArea _oldArea;
    
    public FitToWinAction(final PlainChart theChart,
                          final WorldArea oldArea){
      _theChart = theChart;
      _oldArea = oldArea;
    }

    public boolean isRedoable(){
      return true;
    }
    
    
    public boolean isUndoable(){
      return true;
    }
                 
    public String toString(){
      return "fit to window ";
    }                                        
    
    public void undo()      
    {
      // set the data area for the chart to the old area
      _theChart.getCanvas().getProjection().setDataArea(_oldArea);
      
      // get the projection to refit-itself
  //    _theChart.getCanvas().getProjection().zoom(0.0);
    }
    
    public void execute(){

      // ask the chart to fit to window
      _theChart.rescale();
    }
  }
  
  
}

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainDragTool.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainDragTool.java,v $
// Revision 1.2  2004/05/25 15:43:36  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:41  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-06-09 09:23:21+01  ian_mayo
// refactored to remove extra rubberband parameter in chart drag listener call
//
// Revision 1.3  2003-06-05 16:31:16+01  ian_mayo
// support alternate dragging
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
// Revision 1.1  2001-10-29 12:57:10+00  administrator
// Check we know of our parent
//
// Revision 1.0  2001-07-17 08:43:00+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:57+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:07  ianmayo
// initial version
//
// Revision 1.2  2000-03-14 09:55:15+00  ian_mayo
// process icon names
//
// Revision 1.1  1999-10-12 15:36:26+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-17 08:14:42+01  administrator
// changes to way layers data is passed, and how rubberbands are handled
//
// Revision 1.1  1999-07-27 10:50:32+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:43+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-08 13:08:48+01  administrator
// Initial revision
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

import java.awt.Point;

import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldLocation;

/** abstract class for tools which 'watch' the canvas,
 * normally trapping movements
 */
abstract public class PlainDragTool extends
  PlainTool implements PlainChart.ChartDragListener{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////


  /**
   * keep a reference to the chart we are currently 'watching'
   * */
  private PlainChart _theChart;

  protected WorldLocation _theStart;
  protected WorldLocation _theEnd;

  protected Point _theStartPoint;
  protected Point _theEndPoint;

  private boolean _isAlternateDragger;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  public PlainDragTool(PlainChart theChart,
                         ToolParent theParent,
                         String theLabel,
                         String theImage)
  {
    this(theChart, theParent, theLabel, theImage, false);
  }

  public PlainDragTool(PlainChart theChart,
                         ToolParent theParent,
                         String theLabel,
                         String theImage,
                         boolean isAlternateDragger){

    super(theParent, theLabel, theImage);

    // take copy of the chart we are to listen to
    _theChart = theChart;

    _isAlternateDragger = isAlternateDragger;
  }



  public PlainDragTool()
  {
    super();
    // default constructor, for serialisation
  }


  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  public PlainChart getChart(){
    return _theChart;
  }

  abstract public MWC.GUI.Rubberband getRubberband();

  public void execute(){

    if(_isAlternateDragger)
    {
      _theChart.setAlternateChartDragListener(this);
    }
    else
    {
      // add ourselves as the listener
      _theChart.setChartDragListener(this);
    }

  }


  public void doExecute(Action theAction){
    // start busy
    setCursor(0);

    // do the action
    if(theAction != null)
      theAction.execute();

    // end busy
    restoreCursor();


    if(_theParent != null)
    {
      // add to the undo buffer
      _theParent.addActionToBuffer(theAction);
    }
  }


  public void areaSelected(MWC.GenericData.WorldLocation theLocation, Point thePoint){
    _theEnd = new WorldLocation(theLocation);
    _theEndPoint = thePoint;
  }

  abstract public void startMotion();

  public void startDrag(MWC.GenericData.WorldLocation theLocation, Point thePoint){
    _theStart = new WorldLocation(theLocation);
    _theStartPoint = thePoint;

    // start the operation
    startMotion();
  }


  public void dragging(WorldLocation theLocation, Point thePoint)
  {
    // don't do anything, really
  }

  public void setCursor(int theCursor){
    // check we have a parent
    if(_theParent != null)
    {
      if(theCursor == 0)
        _theParent.setCursor(java.awt.Cursor.WAIT_CURSOR);
      else
        _theParent.setCursor(theCursor);
    }
  }

  public void restoreCursor(){
    if(_theParent != null)
      _theParent.restoreCursor();
  }
}

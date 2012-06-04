package MWC.GUI.Tools.Chart;

// Copyright MWC 1999
// $RCSfile: Pan.java,v $
// $Author: Ian.Mayo $
// $Log: Pan.java,v $
// Revision 1.4  2006/04/21 08:03:28  Ian.Mayo
// Include serial RMI version num
//
// Revision 1.3  2005/09/16 10:02:37  Ian.Mayo
// Make Pan action public
//
// Revision 1.2  2004/02/25 09:26:10  Ian.Mayo
// Improved moving data area
//
// Revision 1.1.1.1  2003/07/17 10:07:43  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-06-06 13:54:39+01  ian_mayo
// Always being set as Alternate, stupid!
//
// Revision 1.3  2003-06-05 16:31:03+01  ian_mayo
// support alternate dragging mode
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
// Revision 1.1  2001-10-29 12:57:21+00  administrator
// Check we have our parent
//
// Revision 1.0  2001-07-17 08:42:57+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-05 09:54:01+00  novatech
// Corrected the name for this operation, and when we do an undo, we want to go back to the OLD area
//
// Revision 1.1  2001-01-03 13:41:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:22  ianmayo
// initial version
//
// Revision 1.4  2000-08-09 16:03:09+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.3  2000-08-07 12:21:45+01  ian_mayo
// tidy icon filename
//
// Revision 1.2  2000-03-14 09:54:49+00  ian_mayo
// use icons for these tools
//
// Revision 1.1  1999-10-12 15:36:21+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-17 08:06:55+01  administrator
// make serializable
//
// Revision 1.2  1999-08-04 09:43:06+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:59:45+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-16 10:01:56+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:15+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//

import java.awt.Point;
import java.io.Serializable;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.PlainChart;
import MWC.GUI.Rubberband;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainDragTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Pan extends PlainDragTool implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  WorldArea _oldArea;
  WorldArea _tmpArea;
  WorldLocation _tmpLocation;

  protected Rubberband _myRubber = new MWC.GUI.RubberBanding.NullRubberBand();


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public Pan(PlainChart theChart,
                         ToolParent theParent,
                         String theImage){
    this(theChart, theParent, theImage, false);
  }

  public Pan(PlainChart theChart,
                         ToolParent theParent,
                         String theImage,
                         boolean isAlternate){
    super(theChart, theParent, "Pan", "images/drag.gif", isAlternate);
  }


  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  public void areaSelected(MWC.GenericData.WorldLocation theLocation, Point thePoint){
    super.areaSelected(theLocation, thePoint);

    super.restoreCursor();

    // we've got to restore the old area in order to calculate
    // the destination position in terms of old coordinates
    // instead of the current screen coordinates
    setNewArea(getChart().getCanvas().getProjection(), _oldArea);

    // now we can do our data/world transform correctly
    _theEnd = getChart().getCanvas().toWorld(thePoint);

    // sort out the vector to apply to the corners
    WorldVector wv = _theStart.subtract(_theEnd);

    // apply this vector to the corners
    WorldLocation currentCentre = _oldArea.getCentre();
    WorldLocation newCentre = currentCentre.add(wv);

    // and store the new area
    WorldArea _newArea = new WorldArea(_oldArea);
    _newArea.setCentre(newCentre);

    super.doExecute(new PanAction(getChart(), _oldArea, _newArea));

  }

  public void startMotion(){
    // store the current area
    _oldArea = getChart().getCanvas().getProjection().getDataArea();

    _tmpArea = new WorldArea(_oldArea);
    _tmpLocation = null;

    MWC.GUI.ToolParent parent = getParent();
    if(parent != null)
      parent.setCursor(java.awt.Cursor.MOVE_CURSOR);
  }


  public String getName(){
    return "Pan tool";
  }

  public Action getData(){
    return null;
  }


  public MWC.GUI.Rubberband getRubberband()
  {
    return _myRubber;
  }


  protected void setNewArea(PlainProjection proj, WorldArea theArea){
    double oldBorder = proj.getDataBorder();
    proj.setDataBorderNoZoom(1.0);
    proj.setDataArea(theArea);
    proj.zoom(0.0);
    proj.setDataBorderNoZoom(oldBorder);
  }


  public void dragging(WorldLocation theLocation, Point thePoint)
  {
    if(_tmpLocation != null){

      // sort out the vector to apply to the corners
      WorldVector wv = _tmpLocation.subtract(theLocation);

      // apply this vector to the corners
      WorldArea newArea = new WorldArea(_tmpArea.getTopLeft().add(wv),
                                         _tmpArea.getBottomRight().add(wv));

      setNewArea(getChart().getCanvas().getProjection(), newArea);

      _tmpArea = getChart().getCanvas().getProjection().getDataArea();

      getChart().update();
    }
    else
      _tmpLocation = new WorldLocation(theLocation);


  }


  //////////////////////////////////////////////////
  // the data for the action
  ///////////////////////////////////////////////////

  public static class PanAction implements Action{

    private PlainChart _theChart;
    private WorldArea _oldArea;
    private WorldArea _newArea;


    public PanAction(PlainChart theChart,
                     WorldArea oldArea,
                     WorldArea newArea){
      _theChart = theChart;
      _oldArea = oldArea;
      _newArea = newArea;
    }

    public boolean isRedoable(){
      return true;
    }

    public boolean isUndoable()
    {
      return true;
    }

    public String toString()
    {
      return "Pan operation";
    }

    public void undo()
    {
      // set the area
      setNewArea(_theChart.getCanvas().getProjection(), _oldArea);
    }

    public void execute()
    {
      // set the area
      setNewArea(_theChart.getCanvas().getProjection(), _newArea);
    }

    protected void setNewArea(PlainProjection proj, WorldArea theArea){
      double oldBorder = proj.getDataBorder();
      proj.setDataBorderNoZoom(1.0);
      proj.setDataArea(theArea);
      proj.zoom(0.0);
      proj.setDataBorderNoZoom(oldBorder);
    }

  }
}

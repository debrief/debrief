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
package MWC.GUI.Tools.Chart;

// Copyright MWC 1999
// $RCSfile: ZoomIn.java,v $
// $Author: Ian.Mayo $
// $Log: ZoomIn.java,v $
// Revision 1.3  2005/09/13 13:45:40  Ian.Mayo
// More accessible if external
//
// Revision 1.2  2004/05/25 15:43:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:43  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:58+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:45+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:58+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:40  ianmayo
// initial version
//
// Revision 1.6  2000-08-07 12:21:48+01  ian_mayo
// tidy icon filename
//
// Revision 1.5  2000-04-19 11:41:54+01  ian_mayo
// reduce size of dragged area necessary to trigger zoom
//
// Revision 1.4  2000-03-14 14:51:46+00  ian_mayo
// minor typo
//
// Revision 1.3  2000-03-14 09:54:50+00  ian_mayo
// use icons for these tools
//
// Revision 1.2  2000-02-03 15:08:21+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.1  1999-10-12 15:36:23+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-08-17 08:14:42+01  administrator
// changes to way layers data is passed, and how rubberbands are handled
//
// Revision 1.2  1999-08-04 09:43:05+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:59:46+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:49+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:15+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:06+01  sm11td
// Initial revision
//

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import MWC.GUI.PlainChart;
import MWC.GUI.Rubberband;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainDragTool;
import MWC.GenericData.WorldArea;

public class ZoomIn extends PlainDragTool  implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  transient WorldArea _oldArea;
  transient WorldArea _newArea;
//  WorldLocation _startLocation;
   
  Rubberband _myRubber = new MWC.GUI.RubberBanding.RubberbandRectangle();
  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public ZoomIn(final PlainChart theChart,
                         final ToolParent theParent){ 
    super(theChart, theParent, "Zoom in", "images/zoomin.gif");
  }
  

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  
  
  public void areaSelected(final MWC.GenericData.WorldLocation theLocation, final Point thePoint){
    super.areaSelected(theLocation, thePoint);
    
    // see if we have selected a worthwhile area
    final Rectangle rt = new Rectangle(_theStartPoint);
    rt.add(_theEndPoint);
    
    if(rt.width + rt.height > 5){
      // ok, go for it
      _newArea = new WorldArea(_theStart, _theEnd);
      _newArea.normalise();
    
      super.doExecute(new ZoomInAction(getChart(), _oldArea, _newArea));
    }
  }
  
  public void startMotion(){
    // store the current area
    _oldArea = getChart().getCanvas().getProjection().getDataArea();
  }
                            
    
  public String getName(){
    return "Zoom tool";
  }

  public Action getData(){
    return null;
  }
  
  //////////////////////////////////////////////////
  // the data for the action
  ///////////////////////////////////////////////////
  
  public static class ZoomInAction implements Action{
    
    private final PlainChart _theChart;
    private final WorldArea _oldArea;
    private final WorldArea _newArea;
   
    
    public ZoomInAction(final PlainChart theChart,
                        final WorldArea oldArea,
                        final WorldArea newArea){
      _theChart = theChart;
      _oldArea = oldArea;
      _newArea = newArea;
    }
                        
    public void dispose()
    {
    	System.err.println("disposing of zoomin action");
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
      return "Zoom in operation";
    }

    public void undo()
    {
      // set the data area for the chart to the old area
      _theChart.getCanvas().getProjection().setDataArea(_oldArea);
      
      // get the projection to refit-itself
  //    _theChart.getCanvas().getProjection().zoom(0.0);
    }

    public void execute()
    {
      // set the data area for the chart to the specified area
      _theChart.getCanvas().getProjection().setDataArea(_newArea);
      
      // get the projection to refit-itself
  //    _theChart.getCanvas().getProjection().zoom(0.0);
    }
  }

  public MWC.GUI.Rubberband getRubberband(){
    return _myRubber;
  }

}

/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// $RCSfile: MoveDirection.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: MoveDirection.java,v $
// Revision 1.2  2004/05/25 15:43:48  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
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
// Revision 1.0  2001-07-17 08:42:56+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:19  ianmayo
// initial version
//

//

package MWC.GUI.Tools.Chart;



import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/** tool to instruct a particular chart to do a resize to fit all
 * of the current data
 */
public class MoveDirection extends PlainTool {
  
  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////  
  
  /** keep a reference to the chart which we are acting upon*/
  private final PlainChart _theChart;  
  
	/** the directions we are allowed to move in
	 */
	public static final int NORTH = 0;
	public static final int EAST  = 1;
	public static final int SOUTH = 2;
	public static final int WEST  = 3;
	public static final int NORTHEAST  = 4;
	public static final int SOUTHEAST  = 5;
	public static final int SOUTHWEST  = 6;
	public static final int NORTHWEST  = 7;
	
	/** the direction for this instance
	 */
	protected int _myDirection;
	
	/** the proportion of the current view that we step by
	 */
	private static final double DISTANCE_FACTOR = 0.05;
	
  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  
  /** constructor, stores information ready for when the button
   * finally gets pressed
   * @param theApp the parent application, so we can set cursors
   * @param theChart the chart we are to resize
   */
  public MoveDirection(final ToolParent theParent, 
											 final PlainChart theChart,
											 final int theDirection,
											 final String theLabel,
											 final String theImage){      
    super(theParent, theLabel, theImage);
		
    // remember the chart we are acting upon
    _theChart = theChart;
		
		_myDirection = theDirection;
  }
  
  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////  
	
	/** return the information we use in our move
	 * @return the Action object representing this step
	 */
  public Action getData()
  {
    // get the current data area
    final WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();

		// working variable for the vector
		double direction = 0;
		double distance = 1;

		// sort out the distance
		distance = oldArea.getHeight() * DISTANCE_FACTOR;
		
		// sort out the direction
		switch (_myDirection) {
			case NORTH:
				direction = 0;
			  break;
			case EAST:
				direction = 90;
			  break;
			case SOUTH:
				direction = 180;
			  break;
			case WEST:
				direction = 270;
			  break;
			case NORTHEAST:
				direction = 45;
			  break;
			case SOUTHEAST:
				direction = 135;
			  break;
			case SOUTHWEST:
				direction = 225;
			  break;
			case NORTHWEST:
				direction = 315;
			  break;
  	}
			
		// convert direction to radians
		direction = MWC.Algorithms.Conversions.Degs2Rads(direction);
		
		// produce the vector
		final WorldVector theVector = new WorldVector(direction,
																						distance,
																						0);
		// convert direction to radians
		direction = MWC.Algorithms.Conversions.Degs2Rads(direction);
		
		// produce the new area
		final WorldLocation tl = oldArea.getTopLeft().add(theVector);
		final WorldLocation br = oldArea.getBottomRight().add(theVector);		
		final WorldArea newArea = new WorldArea(tl, br);
	  
		// produce the action
		return new MoveDirectionAction(_theChart,
																	 oldArea,
																	 newArea);
		
  }

  
  ////////////////////////////////////////////////////////////////////
  // store action information
  protected class MoveDirectionAction implements Action{
    private final PlainChart _theChart1;  
    private final WorldArea _oldArea;
		private final WorldArea _newArea;
    
    public MoveDirectionAction(final PlainChart theChart,
															 final WorldArea oldArea,
															 final WorldArea newArea){
      _theChart1 = theChart;
      _oldArea = oldArea;
			_newArea = newArea;
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
      _theChart1.getCanvas().getProjection().setDataArea(_oldArea);
    }
    
		public void execute()
		{
			// update the data area
   		_theChart1.getCanvas().getProjection().setDataArea(_newArea);
    }
  }
  
  
}

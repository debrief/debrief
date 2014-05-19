package org.mwc.debrief.core.creators.shapes;

import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.VectorShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 *
 */
public class InsertVector extends CoreInsertShape
{


	/** produce the shape for the user
	 * 
	 * @param centre the current centre of the screen
	 * @return a shape, based on the centre
	 */
	protected PlainShape getShape(final WorldLocation centre)
	{
		// generate the shape
		final PlainShape res = new VectorShape(centre,45.0, new WorldDistance(4, WorldDistance.NM));
		return res;
	}	
	
	/** return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	protected String getShapeName()
	{
		return "vector";
	}	
	
}

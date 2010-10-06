/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import MWC.Algorithms.Conversions;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 * 
 */
public class InsertEllipse extends CoreInsertShape
{

	/**
	 * produce the shape for the user
	 * 
	 * @param centre
	 *          the current centre of the screen
	 * @return a shape, based on the centre
	 */
	protected PlainShape getShape(WorldLocation centre)
	{
		// generate the shape
		double maxDegs = Conversions.m2Degs(4000);
		double minDegs = Conversions.m2Degs(1000);
		PlainShape res = new EllipseShape(centre, 45, new WorldDistance(maxDegs,
				WorldDistance.DEGS), new WorldDistance(minDegs, WorldDistance.DEGS));
		return res;
	}

	/**
	 * return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	protected String getShapeName()
	{
		return "ellipse";
	}

}
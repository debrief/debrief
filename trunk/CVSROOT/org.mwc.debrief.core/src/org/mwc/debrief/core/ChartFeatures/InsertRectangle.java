/**
 * 
 */
package org.mwc.debrief.core.ChartFeatures;

import java.awt.Color;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 *
 */
public class InsertRectangle extends CoreInsertShape
{


	protected ShapeWrapper getShape(WorldLocation centre)
	{
		// generate the shape
		ShapeWrapper res = new ShapeWrapper("New rectangle", 
				new RectangleShape(centre, 
						centre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
            0.05, 0))),
				Color.red, null);
		return res;
	}	
}
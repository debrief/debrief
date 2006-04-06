/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import org.mwc.debrief.core.creators.chartFeatures.CoreInsertChartFeature;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.*;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertShape extends CoreInsertChartFeature
{

	/** get a plottable object
	 * 
	 * @param centre
	 * @param theChart
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		
		// right, what's the area we're looking at
		WorldArea wa = theChart.getDataArea();
		
		// get centre of area (at zero depth)
		WorldLocation centre = wa.getCentreAtSurface();

		// create the shape, based on the centre
		PlainShape shape = getShape(centre);

		// and now wrap the shape
		ShapeWrapper theWrapper = new ShapeWrapper("New " + getShapeName(), shape,
				PlainShape.DEFAULT_COLOR, null);
		
		return theWrapper;

	}
	
	/**
	 * @return
	 */
	protected String getLayerName()
	{
		return "Misc";
	}

	/**
	 * produce the shape for the user
	 * 
	 * @param centre
	 *          the current centre of the screen
	 * @return a shape, based on the centre
	 */
	abstract protected PlainShape getShape(WorldLocation centre);
	


	/**
	 * return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	abstract protected String getShapeName();
}
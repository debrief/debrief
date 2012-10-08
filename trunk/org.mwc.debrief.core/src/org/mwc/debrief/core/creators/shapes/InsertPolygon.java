/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import java.util.Vector;

import Debrief.Wrappers.PolygonWrapper;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 * 
 */
public class InsertPolygon extends CoreInsertShape
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
		return null;
	}

	/**
	 * get a plottable object
	 * 
	 * @param centre
	 * @param theChart
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		// get centre of area
		WorldLocation centre = getCentre(theChart);

		// create the shape, based on the centre
		Vector<PolygonNode> path2 = new Vector<PolygonNode>();

		PolygonShape newShape = new PolygonShape(path2);

		// and now wrap the shape
		PolygonWrapper theWrapper = new PolygonWrapper("New " + getShapeName(),
				newShape, PlainShape.DEFAULT_COLOR, null);

		// store the new point
		newShape.add(new PolygonNode("1", centre, (PolygonShape) theWrapper.getShape()));

		return theWrapper;

	}

	/**
	 * return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	protected String getShapeName()
	{
		return "polygon";
	}

}
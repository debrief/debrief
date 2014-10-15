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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
	protected PlainShape getShape(final WorldLocation centre)
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
	protected Plottable getPlottable(final PlainChart theChart)
	{
		// get centre of area
		final WorldLocation centre = getCentre(theChart);

		// create the shape, based on the centre
		final Vector<PolygonNode> path2 = new Vector<PolygonNode>();

		final PolygonShape newShape = new PolygonShape(path2);

		// and now wrap the shape
		final PolygonWrapper theWrapper = new PolygonWrapper("New " + getShapeName(),
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
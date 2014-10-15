/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.creators.shapes;

import MWC.GUI.Shapes.FurthestOnCircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * @author ian.mayo
 * 
 */
public class InsertFurthestOnCircles extends CoreInsertShape
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
		// generate the shape
		final PlainShape res = new FurthestOnCircleShape(centre, 5, new WorldSpeed(10,
				WorldSpeed.Kts), 5 * 60 * 1000, 180, 360);
		return res;
	}

	/**
	 * return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	protected String getShapeName()
	{
		return "Furthest On Circle";
	}

}
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
	protected PlainShape getShape(final WorldLocation centre)
	{
		// generate the shape
		final double maxDegs = Conversions.m2Degs(4000);
		final double minDegs = Conversions.m2Degs(1000);
		final PlainShape res = new EllipseShape(centre, 45, new WorldDistance(maxDegs,
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
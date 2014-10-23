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
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;

public interface IGASolutionsListener extends IGenerateSolutionsListener
{
	
	/** learn that an iteration is complete
	 * 
	 * @param topRoutes the top performing routes for this iteration
	 * @param topScore the score of the single best performing route
	 */
	void iterationComputed(List<CompositeRoute> topRoutes, double topScore);
}

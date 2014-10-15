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
package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;

public interface IGenerateSolutionsListener
{

	/**
	 * we have some solutions
	 * @param routes 
	 * 
	 */
	void solutionsReady(CompositeRoute[] routes);
	
	/** we're about to start generating solutions
	 * 
	 */
	void startingGeneration();
	
	/**
	 * solution generator finished generation. 
	 * 
	 * If generation job finished successfully it will be called 
	 * after solutionsReady. 
	 * 
	 * If generation job was canceled or finished with error
	 * this method will be called after jobManager closes 
	 * all job resources   
	 */
	void finishedGeneration(Throwable error);
}
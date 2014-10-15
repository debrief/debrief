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
package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

/**
 * interface to generate solutions on constrained problem space 
 */
public interface ISolutionGenerator
{

	/** someone's interested in solution generation
	 * 
	 * @param listener
	 */
	void addReadyListener(IGenerateSolutionsListener listener);

	/** someone's no longer interested in solution generation
	 * 
	 * @param listener
	 */
	void removeReadyListener(IGenerateSolutionsListener listener);
	
	/** specify how detailed the maths should be
	 * 
	 * @param precision
	 */
	void setPrecision(Precision precision);
	
	/**
	 * returns current precision
	 */
	Precision getPrecision();
	
	/** whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @param autoSuppress yes/no
	 */
	void setAutoSuppress(boolean autoSuppress);
	
	/** whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @return yes/no
	 */
	boolean getAutoSuppress();
	
	
	/**
	 * returns problem space which is used by solution generator  
	 */
	SafeProblemSpace getProblemSpace();
	
	/**
	 * clears 
	 */
	void clear();
	
	/**
	 * starts generate solutions job
	 */
	void generateSolutions(boolean fullRerun);
	
	/**
	 * cancels generate solutions job
	 */
	void cancel();
}
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

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public interface IConstrainSpaceListener
{

	/**
	 * bound the states is complete
	 * 
	 */
	void statesBounded(IBoundsManager boundsManager);

	/**
	 * the sequence has restarted
	 * 
	 */
	void restarted(IBoundsManager boundsManager);

	/**
	 * error was appeared during processing
	 * 
	 */
	void error(IBoundsManager boundsManager, IncompatibleStateException ex);

	/**
	 * a step has been performed
	 * 
	 * @param thisStep
	 *          the index of this step (zero-origin)
	 * @param totalSteps
	 *          the total number of steps
	 */
	void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps);
}
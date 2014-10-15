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

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.impl.ga.IGASolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class SteppingAdapter implements
		IConstrainSpaceListener,
		IGenerateSolutionsListener,
		IGASolutionsListener
{

	@Override
	public void statesBounded(IBoundsManager boundsManager)
	{
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
	}

	@Override
	public void solutionsReady(CompositeRoute[] routes)
	{
	}

	@Override
	public void startingGeneration()
	{
	}

	@Override
	public void finishedGeneration(Throwable error)
	{
	}

	@Override
	public void iterationComputed(List<CompositeRoute> topRoutes, double topScore)
	{
	}
}

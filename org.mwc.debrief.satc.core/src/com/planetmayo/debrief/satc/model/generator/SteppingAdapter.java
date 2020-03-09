/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.generator;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.impl.ga.IGASolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class SteppingAdapter implements IConstrainSpaceListener, IGenerateSolutionsListener, IGASolutionsListener {

	@Override
	public void error(final IBoundsManager boundsManager, final IncompatibleStateException ex) {
	}

	@Override
	public void finishedGeneration(final Throwable error) {
	}

	@Override
	public void iterationComputed(final List<CompositeRoute> topRoutes, final double topScore) {
	}

	@Override
	public void restarted(final IBoundsManager boundsManager) {
	}

	@Override
	public void solutionsReady(final CompositeRoute[] routes) {
	}

	@Override
	public void startingGeneration() {
	}

	@Override
	public void statesBounded(final IBoundsManager boundsManager) {
	}

	@Override
	public void stepped(final IBoundsManager boundsManager, final int thisStep, final int totalSteps) {
	}
}

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

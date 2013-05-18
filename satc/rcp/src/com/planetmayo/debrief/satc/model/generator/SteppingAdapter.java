package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class SteppingAdapter implements
		IConstrainSpaceListener,
		IGenerateSolutionsListener
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
}

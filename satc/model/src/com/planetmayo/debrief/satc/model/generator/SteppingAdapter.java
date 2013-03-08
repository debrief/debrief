package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class SteppingAdapter implements
		ISteppingListener.IConstrainSpaceListener,
		ISteppingListener.IGenerateSolutionsListener
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
	public void legsDiced()
	{
	}

	@Override
	public void legsGenerated()
	{
	}

	@Override
	public void legsScored()
	{
	}

	@Override
	public void solutionsReady(CompositeRoute[] routes)
	{
	}
}

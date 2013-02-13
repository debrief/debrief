package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class SolutionGenerator implements ISteppingListener
{
	public SolutionGenerator()
	{
		
	}

	@Override
	public void complete(IBoundsManager boundsManager)
	{
		// ok - it's complete. now we can process it
		@SuppressWarnings("unused")
		ProblemSpace space = boundsManager.getSpace();
		
		// right, get to work.
		
		// extract the straight legs
		
		// loop through the legs
		
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
		// restarted, clear out any temp storage
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
		// gen contributions failed
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		// step forward in generated solutions. We should prob ignore this
	}
}

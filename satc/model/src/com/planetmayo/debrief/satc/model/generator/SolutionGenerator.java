package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.StraightLeg;

public class SolutionGenerator implements ISteppingListener
{
	public SolutionGenerator()
	{

	}

	@Override
	public void complete(IBoundsManager boundsManager)
	{
		// ok - it's complete. now we can process it
		ProblemSpace space = boundsManager.getSpace();

		// right, get to work.

		// TODO: also extract the altering legs
		
		// extract the straight legs
		HashMap<String, StraightLeg> straightLegs = new HashMap<String, StraightLeg>();

		StraightLeg thisLeg;

		Collection<BoundedState> theStates = space.states();
		for (Iterator<BoundedState> iterator = theStates.iterator(); iterator.hasNext();)
		{
			BoundedState thisS = iterator.next();
			String thisLegName = thisS.getMemberOf();

			if (thisLegName != null)
			{
				// ok, do we have a straight leg for this name
				thisLeg = straightLegs.get(thisLegName);

				if (thisLeg == null)
				{
					thisLeg = new StraightLeg(thisLegName,new ArrayList<BoundedState>() );
					straightLegs.put(thisLegName, thisLeg);
				}
				
				// ok, we've got the leg - now add the state
				thisLeg.add(thisS);
			}
		}
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

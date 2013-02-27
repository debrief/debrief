package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class SolutionGenerator implements ISteppingListener
{
	final int NUM_CELLS=20;
	
	public SolutionGenerator()
	{

	}

	@Override
	public void complete(IBoundsManager boundsManager)
	{
		// ok - it's complete. now we can process it
		ProblemSpace space = boundsManager.getSpace();

		// get the legs
		HashMap<String, CoreLeg> theLegs = getTheLegs(space);
		
		
		// get the legs to dice themselves up
		operateOn(theLegs, new LegStepper(){
			public void apply(CoreLeg thisLeg)
			{
				thisLeg.generateRoutes(NUM_CELLS);
			}});
		
		
		// get the legs to sort out what is achievable
		
		// do the fancy multiplication
		
		// ditch the duff permutations
		
		// score the possible routes
		
		// generate some candidate solutions
		
		// and we're done!
		
		// TODO: also extract the altering legs
		
	}
	
	private static void operateOn(HashMap<String, CoreLeg> theLegs, LegStepper theStepper)
	{
		Collection<CoreLeg> iter = theLegs.values();
		for (Iterator<CoreLeg> iterator = iter.iterator(); iterator.hasNext();)
		{
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			theStepper.apply(thisLeg);
		}
	}
	
	
	
	public static interface LegStepper
	{
		/** operate on this leg
		 * 
		 * @param thisLeg
		 */
		public void apply(CoreLeg thisLeg);
	}

	private HashMap<String, CoreLeg> getTheLegs(ProblemSpace space)
	{

		// extract the straight legs
		HashMap<String, CoreLeg> straightLegs = new HashMap<String, CoreLeg>();

		CoreLeg currentLeg = null;

		Collection<BoundedState> theStates = space.states();
		for (Iterator<BoundedState> iterator = theStates.iterator(); iterator.hasNext();)
		{
			BoundedState thisS = iterator.next();
			String thisLegName = thisS.getMemberOf();

			if (thisLegName != null)
			{
				// ok, do we have a straight leg for this name
				currentLeg = straightLegs.get(thisLegName);

				if (currentLeg == null)
				{
					currentLeg = new StraightLeg(thisLegName,new ArrayList<BoundedState>() );
					straightLegs.put(thisLegName, currentLeg);
				}
				
				// ok, we've got the leg - now add the state
				currentLeg.add(thisS);
			}
		}
		return straightLegs;
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

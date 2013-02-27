package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.legs.AlteringLeg;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class SolutionGenerator implements ISteppingListener
{
	final int NUM_CELLS = 20;

	public SolutionGenerator()
	{

	}

	@Override
	public void complete(IBoundsManager boundsManager)
	{
		// ok - it's complete. now we can process it
		ProblemSpace space = boundsManager.getSpace();

		// get the legs
		HashMap<String, CoreLeg> theLegs = getTheLegs(space.states());

		// get the legs to dice themselves up
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				thisLeg.generateRoutes(NUM_CELLS);
			}
		});

		// get the legs to sort out what is achievable
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				thisLeg.decideAchievableRoutes();
			}
		});

		// do the fancy multiplication
		int[][] achievableRes = calculateAchievableRoutesFor(theLegs);

		// ditch the duff permutations
		cancelUnachievable(theLegs, achievableRes);

		// score the possible routes
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				if (thisLeg.getType() == LegType.STRAIGHT)
				{
					StraightLeg leg = (StraightLeg) thisLeg;
					leg.calculateOptimum();
				}
			}
		});

		// generate some candidate solutions

		// and we're done!

		// TODO: also extract the altering legs

	}

	static void cancelUnachievable(HashMap<String, CoreLeg> theLegs,
			int[][] achievableRes)
	{
		// TODO Auto-generated method stub

	}

	static int[][] calculateAchievableRoutesFor(HashMap<String, CoreLeg> theLegs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * apply the specified operation on all legs
	 * 
	 * @param theLegs
	 * @param theStepper
	 */
	private static void operateOn(HashMap<String, CoreLeg> theLegs,
			LegOperation theStepper)
	{
		Collection<CoreLeg> iter = theLegs.values();
		for (Iterator<CoreLeg> iterator = iter.iterator(); iterator.hasNext();)
		{
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			theStepper.apply(thisLeg);
		}
	}

	/**
	 * utility interface to make it easy to operate on all legs
	 * 
	 * @author Ian
	 * 
	 */
	public static interface LegOperation
	{
		/**
		 * operate on this leg
		 * 
		 * @param thisLeg
		 */
		public void apply(CoreLeg thisLeg);
	}

	/**
	 * extract a set of legs from the space
	 * 
	 * @param space
	 * @return
	 */
	static HashMap<String, CoreLeg> getTheLegs(Collection<BoundedState> theStates)
	{

		// extract the straight legs
		HashMap<String, CoreLeg> theLegs = new HashMap<String, CoreLeg>();

		CoreLeg currentLeg = null;
		
		// increementing counter, to number turns
		int counter = 1;

		Iterator<BoundedState> iterator = theStates.iterator();
		while (iterator.hasNext())
		{
			BoundedState thisS = iterator.next();
			String thisLegName = thisS.getMemberOf();
			
			// is this the current leg?
			if (thisLegName != null)
			{
				// ok, do we have a straight leg for this name
				currentLeg = theLegs.get(thisLegName);

				if (currentLeg == null)
				{
					currentLeg = new StraightLeg(thisLegName,
							new ArrayList<BoundedState>());
					theLegs.put(thisLegName, currentLeg);
				}
			}
			else
			{
				// a leg with no name = must be altering
				
				// were we in a straight leg?
				if(currentLeg != null)
				{
					if(currentLeg.getType() == LegType.STRAIGHT)
					{
						// ok, the straight leg is now complete. trigger a new altering leg
						currentLeg = null;
					}
				}
				
				// ok, are we currently in a leg?
				if(currentLeg == null)
				{
					String thisName = "Alteration " + counter++;
					currentLeg = new AlteringLeg(thisName, new ArrayList<BoundedState>());
					theLegs.put(thisName,currentLeg);
				}
			}
			
			// ok, we've got the leg - now add the state
			currentLeg.add(thisS);
		}
		return theLegs;
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

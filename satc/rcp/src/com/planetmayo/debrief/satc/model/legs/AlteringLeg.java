package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class AlteringLeg extends CoreLeg
{

	/**
	 * create an altering leg.
	 * 
	 * @param name
	 *          what to call the leg
	 * @param states
	 *          the set of bounded states that comprise the leg
	 */
	public AlteringLeg(String name, ArrayList<BoundedState> states)
	{
		super(name, states);
	}

	/**
	 * use a simple speed/time decision to decide if it's possible to navigate a
	 * route
	 */
	public void decideAchievableRoute(CoreRoute r)
	{
		if (! (r instanceof AlteringRoute)) {
			return;
		}
		AlteringRoute route = (AlteringRoute) r; 
		SpeedRange speedRange = _states.get(0).getSpeed();
		double distance = route.getDirectDistance();
		double elapsed = route.getElapsedTime();
		double speed = distance / elapsed;

		if (!speedRange.allows(speed))
		{
			route.setImpossible();
		}		
	}

	@Override
	public LegType getType()
	{
		return LegType.ALTERING;
	}

	/**
	 * run through all the route permutation, and find the one with the highest
	 * score(s)
	 * 
	 */
	public void calculateOptimum()
	{
		// TODO calculate an optimal solution through this manoeuvre

	}
}
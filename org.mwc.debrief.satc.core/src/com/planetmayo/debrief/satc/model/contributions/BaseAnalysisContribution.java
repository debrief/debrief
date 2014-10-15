/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public abstract class BaseAnalysisContribution<R extends BaseRange<?>> extends
		BaseContribution
{
	private static final long serialVersionUID = 1L;

	@Override
	final public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		ArrayList<BoundedState> states = new ArrayList<BoundedState>(space.states());
		// do a forward pass through the list
		applyAnalysisConstraints(states, space.getVehicleType());
		
		// and now a reverse pass
		Collections.reverse(states);
		applyAnalysisConstraints(states, space.getVehicleType());
	}

	protected void applyAnalysisConstraints(List<BoundedState> states, VehicleType vType) throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState lastStateWithState = null;

		// remember the current bounded range
		R currentLegRange = null;

		// keep track of the states in this leg - so we can apply the common range
		// bounds to them
		final ArrayList<BoundedState> statesInThisLeg = new ArrayList<BoundedState>();

		// ok, loop through the states, setting range limits for any unbounded
		// ranges
		for (BoundedState currentState : states)
		{
			// ok - what leg is this?
			String thisLeg = currentState.getMemberOf();

			// is it even in a leg?
			if (thisLeg == null)
			{
				// ok, have we just finished a leg?
				if (!statesInThisLeg.isEmpty())
				{
					assignCommonState(currentLegRange, statesInThisLeg);
					currentLegRange = null;
				}
				// ok, we're not in a leg. relax it.
				lastStateWithState = applyRelaxedRangeBounds(lastStateWithState,
							currentState, vType);
			}
			else
			{
				// ok, we're in a leg - see if this is the first point (in which case we
				// allow relaxation),
				// or a successive point (in which case we just constrain)

				// is this the first item in this leg
				if (statesInThisLeg.isEmpty())
				{
					// yes. this is the first leg. We do need to allow some relaxation
					// on range from the previous one
					lastStateWithState = applyRelaxedRangeBounds(lastStateWithState,
								currentState, vType);
				}
				R thisRange = getRangeFor(currentState);				
				if (thisRange != null)
				{
					if (currentLegRange == null)
					{
						// ok, store it
						currentLegRange = cloneRange(thisRange);
					}
					else
					{
						furtherConstrain(currentLegRange, thisRange);
					}
					lastStateWithState = currentState;
				}
				// ok, remmber this leg
				statesInThisLeg.add(currentState);
			}
		}

		// ok, do we have a dangling set of states to be assigned?
		if (!statesInThisLeg.isEmpty())
		{
			assignCommonState(currentLegRange, statesInThisLeg);
			currentLegRange = null;
		}
	}

	/**
	 * apply this bounded constraint to all to all of the states in the list
	 * 
	 * @param commonRange
	 * @param statesInThisLeg
	 * @throws IncompatibleStateException
	 */
	protected void assignCommonState(R commonRange,
			ArrayList<BoundedState> statesInThisLeg) throws IncompatibleStateException
			
	{
		// ok - apply the common bounded range to all the states in the leg	
		// have we produced a constrained range?
		if (commonRange != null)
		{
			// yes. we need to apply the min constraints to all the points in
			// that leg
			Iterator<BoundedState> iter = statesInThisLeg.iterator();
			while (iter.hasNext())
			{
				BoundedState boundedState = (BoundedState) iter.next();
				applyThis(boundedState, commonRange);
			}
		}	
		// now we need to clear the remembered states, so we're ready for the
		// next leg
		statesInThisLeg.clear();
	}

	protected BoundedState applyRelaxedRangeBounds(
			BoundedState lastStateWithRange, BoundedState currentState,
			VehicleType vehicleType) throws IncompatibleStateException
	{
		R newRange = null;
		
		// do we have vehicle reference data and last state?
		if (vehicleType != null && lastStateWithRange != null)
		{
			// ok, how long since that last observation?
			long millis = currentState.getTime().getTime()
					- lastStateWithRange.getTime().getTime();
	
			// ok, what does that state relax to
		  newRange = calcRelaxedRange(lastStateWithRange, vehicleType, millis);
			
			// ok, apply this new constraint, or further constrain any existing one
			applyThis(currentState, newRange);
		}
		// retrieve our range
		R thisRange = getRangeFor(currentState);		
		// is there a range for our type?
		if (thisRange != null)
		{
			// yes - we can use this state
			return currentState;
		}
		return null;	
	}
	
	protected abstract void applyThis(BoundedState state, R thisState)
			throws IncompatibleStateException;	

	/**
	 * apply our range to this existing one
	 * 
	 * @param currentLegRange
	 * @param thisRange
	 * @throws IncompatibleStateException
	 */
	abstract protected void furtherConstrain(R currentLegRange, R thisRange)
			throws IncompatibleStateException;

	/**
	 * run the copy constructor for this range
	 * 
	 * @param thisRange
	 * @return
	 */
	protected abstract R cloneRange(R thisRange);

	/**
	 * extract my range type from this state
	 * 
	 * @param lastStateWithRange
	 * @return
	 */
	abstract protected R getRangeFor(BoundedState lastStateWithRange);

	/** ok, how far does the range relax after the specified period
	 * 
	 * @param lastStateWithRange the last known state with contraints for our range
	 * @param vType the vehicle type data
	 * @param millis how long has elapsed
	 * @return the new bounded state
	 */
	abstract protected R calcRelaxedRange(BoundedState lastStateWithRange,
			VehicleType vType, long millis);
}

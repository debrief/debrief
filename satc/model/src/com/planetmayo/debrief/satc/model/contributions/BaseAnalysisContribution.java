package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public abstract class BaseAnalysisContribution<R extends BaseRange<?>> extends
		BaseContribution
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * apply this bounded constraint to all to all of the states in the list
	 * 
	 * @param commonRange
	 * @param statesInThisLeg
	 * @throws IncompatibleStateException
	 */
	protected void assignCommonState(R commonRange,
			ArrayList<BoundedState> statesInThisLeg)
			throws IncompatibleStateException
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

	protected abstract void applyThis(BoundedState state, R thisState)
			throws IncompatibleStateException;

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState lastStateWithState = null;

		// remember the current bounded range
		R currentLegRange = null;

		// keep track of the states in this leg - so we can apply the common range
		// bounds to them
		final ArrayList<BoundedState> statesInThisLeg = new ArrayList<BoundedState>();

		// get the vehicle type
		final VehicleType vType = space.getVehicleType();

		// ok, loop through the states, setting range limits for any unbounded
		// ranges
		for (BoundedState currentState : space.states())
		{
			String thisLeg = currentState.getMemberOf();

			// ok, is this in a leg?
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

					// now we can probably store this state
					if (lastStateWithState != null)
						currentLegRange = getRangeFor(lastStateWithState);
				}
				else
				{
					// ok, we won't be relaxing this leg - we're just going to further
					// constrain the range

					// does this have a range consraint?
					R thisRange = getRangeFor(currentState);
					if (thisRange != null)
					{
						if (currentLegRange == null)
						{
							// ok, store it
							currentLegRange = duplicateThis(thisRange);
						}
						else
						{
							// ok, constrain it.
							furtherConstrain(currentLegRange, thisRange);
						}
					}

					// but, we do need to know the DTG and range of the last
					// state that had both. If we've ever had a range constraint,
					// then we know we're able to use this state for the DTG.
					if (lastStateWithState != null)
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

	/** apply our range to this existing one
	 * 
	 * @param currentLegRange
	 * @param thisRange
	 * @throws IncompatibleStateException
	 */
	abstract protected void furtherConstrain(R currentLegRange, R thisRange)
			throws IncompatibleStateException;

	/** run the copy constructor for this range
	 * 
	 * @param thisRange
	 * @return
	 */
	protected abstract R duplicateThis(R thisRange);

	/** extract my range type from this state
	 * 
	 * @param lastStateWithRange
	 * @return
	 */
	abstract protected R getRangeFor(BoundedState lastStateWithRange);

	public BoundedState applyRelaxedRangeBounds(BoundedState lastStateWithRange,
			BoundedState currentState, VehicleType vehicleType)
			throws IncompatibleStateException
	{
		// do we have vehicle reference data?
		if (vehicleType != null)
		{

			// do we have a previous state?
			if (lastStateWithRange != null)
			{
				// ok, how long since that last observation?
				long millis = currentState.getTime().getTime()
						- lastStateWithRange.getTime().getTime();

				R newRange = calcRelaxedRange(lastStateWithRange, vehicleType, millis);

				// and create a constraint for the new valus
				applyThis(currentState, newRange);

			}
		}

		BoundedState newLastState = null;

		// did we find one?
		R thisRange = getRangeFor(currentState);
		if (thisRange != null)
		{
			newLastState = currentState;
		}
		else
		{
			newLastState = lastStateWithRange;
		}

		return newLastState;

	}

	abstract protected R calcRelaxedRange(BoundedState lastStateWithRange,
			VehicleType vType, long millis);

}

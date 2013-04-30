package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

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

	protected abstract void applyThis(BoundedState state, R thisState)
			throws IncompatibleStateException;

	@Override
	final public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// do a forward pass through the list
		applyAnalysisConstraints(space, new SwitchableIterator(true));
		
		// and now a reverse pass
		// NOTE: we've commented this out, since it's cancelling legitimate solutions.
		// reinstate as required in the future.
	//	applyAnalysisConstraints(space, new SwitchableIterator(false));
	}

	/** support class that lets use move either forwards or backwards through a list
	 * 
	 * @author Ian
	 *
	 */
	protected static class SwitchableIterator
	{
		private final boolean fwd;

		protected SwitchableIterator(boolean fwd)
		{
			this.fwd = fwd;
		}

		protected ListIterator<BoundedState> getIterator(ArrayList<BoundedState> states )
		{
			if(fwd)
				return states.listIterator();
			else
				return states.listIterator(states.size());
		}
		
		protected BoundedState next(ListIterator<BoundedState> iter)
		{
			if (fwd)
				return iter.next();
			else
				return iter.previous();
		}

		protected boolean canStep(ListIterator<BoundedState> iter)
		{
			if (fwd)
				return iter.hasNext();
			else
				return iter.hasPrevious();
		}
	}

	protected void applyAnalysisConstraints(final ProblemSpace space,
			SwitchableIterator switcher) throws IncompatibleStateException
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
		Collection<BoundedState> theStates = space.states();
		ArrayList<BoundedState> al = new ArrayList<BoundedState>(theStates);
		ListIterator<BoundedState> iter = switcher.getIterator(al);
		while(switcher.canStep(iter))
		{
			BoundedState currentState = switcher.next(iter);

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
				try
				{
					lastStateWithState = applyRelaxedRangeBounds(lastStateWithState,
							currentState, vType);
				}
				catch (IncompatibleStateException e)
				{
					e.setFailingState(currentState);
					throw e;
				}
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
					try
					{
						lastStateWithState = applyRelaxedRangeBounds(lastStateWithState,
								currentState, vType);
					}
					catch (IncompatibleStateException e)
					{
						e.setFailingState(currentState);
						throw e;
					}

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
							try
							{
								furtherConstrain(currentLegRange, thisRange);
							}
							catch (IncompatibleStateException e)
							{
								e.setFailingState(currentState);
								throw e;
							}
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
				try
				{
					applyThis(boundedState, commonRange);
				}
				catch (IncompatibleStateException e)
				{
					e.setFailingState(boundedState);
					throw e;
				}
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
		
		// do we have vehicle reference data?
		if (vehicleType != null)
		{
	
			// do we have a previous state?
			if (lastStateWithRange != null)
			{
				// ok, how long since that last observation?
				long millis = currentState.getTime().getTime()
						- lastStateWithRange.getTime().getTime();
	
				// ok, what does that state relax to
			  newRange = calcRelaxedRange(lastStateWithRange, vehicleType, millis);
			
				// ok, apply this new constraint, or further constrain any existing one
				applyThis(currentState, newRange);

			}
		}
	
		final BoundedState newLastState;
	
		// ok, now a tricky bit. We have to find out if we have a new state object that
		// has a constraint in our range type.
		
		// retrieve our range
		R thisRange = getRangeFor(currentState);
		
		// is there a range for our type?
		if (thisRange != null)
		{
			// yes - we can use this state
			newLastState = currentState;
		}
		else
		{
			// have we calculated a relaxed range?
			if(newRange != null)
				// yes, use it
				newLastState =  currentState;
			else
				// now, lose the constraint
				newLastState= null;
		}
	
		return newLastState;
	
	}

	abstract protected void relaxConstraint(BoundedState currentState, R newRange);

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
	protected abstract R duplicateThis(R thisRange);

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

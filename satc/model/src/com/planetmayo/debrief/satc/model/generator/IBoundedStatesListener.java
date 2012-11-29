package com.planetmayo.debrief.satc.model.generator;

import java.util.Collection;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

/**
 * listener for when the set of states have been bounded
 * 
 * @author ian
 * 
 */
public interface IBoundedStatesListener
{

	/**
	 * a contribution has acted upon the set of bounded states, this is mostly
	 * intended for a debug-mode style of operation
	 * 
	 * @param newStates
	 */
	public void debugStatesBounded(Collection<BoundedState> newStates);

	/**
	 * an incompatible set of states have been identified
	 * 
	 * @param contribution
	 *          the contribution that triggered the mal-a-state
	 * @param e
	 *          the description of hte problem
	 */
	public void incompatibleStatesIdentified(BaseContribution contribution,
			IncompatibleStateException e);

	/**
	 * the set of states have been defined, and bounded
	 * 
	 * @param newStates
	 */
	public void statesBounded(Collection<BoundedState> newStates);

}

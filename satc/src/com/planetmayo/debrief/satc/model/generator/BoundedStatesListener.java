package com.planetmayo.debrief.satc.model.generator;

import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

/** listener for when the set of states have been bounded
 * 
 * @author ian
 *
 */
public interface BoundedStatesListener
{
	/** the set of states have been defined, and bounded
	 * 
	 * @param newStates
	 */
	public void statesBounded(Iterator<BoundedState> newStates);

	/** an incompatible set of states have been identified
	 * 
	 * @param e the description of hte problem
	 */
	public void incompatibleStatesIdentified(IncompatibleStateException e);
	
}

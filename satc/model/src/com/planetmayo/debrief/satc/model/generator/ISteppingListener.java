package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

/**
 * interface for anybody that wants to know about a stepping generator
 * 
 * @author ian
 * 
 */
public interface ISteppingListener
{
	/**
	 * stepping is complete
	 * 
	 */
	public void complete(BoundsManager boundsManager);

	/**
	 * the sequence has restarted
	 * 
	 */
	public void restarted(BoundsManager boundsManager);
	
	/**
	 * error was appeared during processing
	 * 
	 */
	public void error(BoundsManager boundsManager, IncompatibleStateException ex);	

	/**
	 * a step has been performed
	 * 
	 * @param thisStep
	 *          the index of this step (zero-origin)
	 * @param totalSteps
	 *          the total number of steps
	 */
	public void stepped(BoundsManager boundsManager, int thisStep, int totalSteps);
}
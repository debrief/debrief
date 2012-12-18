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
	void complete(IBoundsManager boundsManager);

	/**
	 * the sequence has restarted
	 * 
	 */
	void restarted(IBoundsManager boundsManager);
	
	/**
	 * error was appeared during processing
	 * 
	 */
	void error(IBoundsManager boundsManager, IncompatibleStateException ex);	

	/**
	 * a step has been performed
	 * 
	 * @param thisStep
	 *          the index of this step (zero-origin)
	 * @param totalSteps
	 *          the total number of steps
	 */
	void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps);
}
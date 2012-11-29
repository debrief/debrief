package com.planetmayo.debrief.satc.model.generator;

/**
 * interface for anybody that wants to know about a stepping generator
 * 
 * @author ian
 * 
 */
public interface SteppingListener
{
	/**
	 * stepping is complete
	 * 
	 */
	public void complete();

	/**
	 * the sequence has restarted
	 * 
	 */
	public void restarted();

	/**
	 * a step has been performed
	 * 
	 * @param thisStep
	 *          the index of this step (zero-origin)
	 * @param totalSteps
	 *          the total number of steps
	 */
	public void stepped(int thisStep, int totalSteps);
}
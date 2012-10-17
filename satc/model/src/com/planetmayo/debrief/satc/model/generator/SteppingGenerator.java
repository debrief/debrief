package com.planetmayo.debrief.satc.model.generator;

/**
 * interface for how to run a stepping data generator
 * 
 * @author ian
 * 
 */
public interface SteppingGenerator
{
	/**
	 * interface for anybody that wants to know about a stepping generator
	 * 
	 * @author ian
	 * 
	 */
	public static interface SteppingListener
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
		 * a step has made
		 * 
		 * @param thisStep
		 *          the index of this step (zero-origin)
		 * @param totalSteps
		 *          the total number of steps
		 */
		public void stepped(int thisStep, int totalSteps);
	}

	/**
	 * restart the set of contributions
	 * 
	 */
	public void restart();

	/**
	 * run through the remaining contributions
	 * 
	 */
	public void run();

	/**
	 * move to the next contribution
	 * 
	 */
	public void step();
}

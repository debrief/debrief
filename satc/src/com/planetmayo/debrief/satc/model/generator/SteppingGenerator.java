package com.planetmayo.debrief.satc.model.generator;

/** interface for how to run a stepping data generator
 * 
 * @author ian
 *
 */
public interface SteppingGenerator
{
	/** restart the set of contributions
	 * 
	 */
	public void restart();
	
	/** move to the next contribution
	 * 
	 */
	public void step();
	
	/** run through the remaining contributions
	 * 
	 */
	public void run();
	
	/** interface for anybody that wants to know about a stepping generator
	 * 
	 * @author ian
	 *
	 */
	public static interface SteppingListener
	{
		/** a step has made
		 * 
		 * @param thisStep the index of this step (zero-origin)
		 * @param totalSteps the total number of steps
		 */
		public void stepped(int thisStep, int totalSteps);
		
		/** the sequence has restarted
		 * 
		 */
		public void restarted();
		
		/** stepping is complete
		 * 
		 */
		public void complete();
	}
}

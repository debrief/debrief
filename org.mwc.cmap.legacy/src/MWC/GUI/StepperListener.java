
package MWC.GUI;

import MWC.GenericData.HiResDate;

public interface StepperListener
{
	/**
	 * the mode for stepping has changed
	 */
	void steppingModeChanged(boolean on);

	/**
	 * the current time has changed
	 */
	void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas);

	/**
	 * the name of this listener
	 */
	String toString();
	
	/** we no longer have a valid time period
	 * 
	 */
	void reset();

	/**
	 * embedded interface for an actual stepper controller (such as the Debrief
	 * Step Control)
	 */
	static public interface StepperController
	{
		/**
		 * add a new listener
		 */
		public void addStepperListener(StepperListener listener);

		/**
		 * remove a listener
		 */
		public void removeStepperListener(StepperListener listener);

		/**
		 * instruct the stepper to move forwards, backwards
		 */
		public void doStep(boolean forward, boolean large_step);

		/**
		 * find out what the current time is
		 */
		public HiResDate getCurrentTime();

		/**
		 * determine the zero-time or anchor for the current dataset
		 */
		public HiResDate getTimeZero();
	}
}

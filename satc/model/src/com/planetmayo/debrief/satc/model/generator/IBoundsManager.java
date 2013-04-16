package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/**
 * interface for how to run a stepping data generator
 * 
 * @author ian
 * 
 */
public interface IBoundsManager
{
	/**
	 * subscribe to progress events
	 */
	void addConstrainSpaceListener(IConstrainSpaceListener newListener);

	/**
	 * unsubscribe from progress events
	 */
	void removeConstrainSpaceListener(IConstrainSpaceListener newListener);

	/**
	 * store the vehicle type, restart the process if started
	 * 
	 * @param v
	 *          the new vehicle type
	 */
	void setVehicleType(VehicleType v);

	/**
	 * restart the set of contributions
	 * 
	 */
	void restart();

	/**
	 * run through the remaining contributions
	 * 
	 */
	void run();

	/**
	 * move to the next contribution
	 * 
	 */
	void step();

	/**
	 * @return contribution which was processed on current step
	 */
	BaseContribution getCurrentContribution();

	/**
	 * @return current step number
	 */
	int getCurrentStep();

	/**
	 * 
	 * @return is bounds manager already processed all contributions
	 */
	boolean isCompleted();

	/**
	 * control what diagnostic data is broadcast during constrain problem space
	 * phase
	 * 
	 * @author ian
	 * 
	 */
	public static interface IShowBoundProblemSpaceDiagnostics
	{
		/**
		 * broadcast all of the bounded states
		 * 
		 * @param onOff
		 */
		public void setShowAllBounds(boolean onOff);

		/**
		 * broadcast just the leg end states
		 * 
		 * @param onOff
		 */
		public void setShowLegEndBounds(boolean onOff);
	}

	/**
	 * control what diagnostic data is broadcast during generate solutions phase
	 * 
	 * @author ian
	 * 
	 */
	public static interface IShowGenerateSolutionsDiagnostics
	{
		/**
		 * whether to show the whole grid of start/end points
		 * 
		 * @param onOff
		 */
		public void setShowPoints(boolean onOff);

		/**
		 * whether to just show the achievable start/end points
		 * 
		 * @param onOff
		 */
		public void setShowAchievablePoints(boolean onOff);

		/**
		 * whether to show the achievable routes inside legs
		 * 
		 * @param onOff
		 */
		public void setShowRoutes(boolean onOff);

		/**
		 * whether to show the processed routes, including scores
		 * 
		 * @param onOff
		 */
		public void setShowRoutesWithScores(boolean onOff);

		/**
		 * whether to show the final recommended solution(s)
		 * 
		 * @param onOff
		 */
		public void setShowRecommendedSolutions(boolean onOff);

	}
}

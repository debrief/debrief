package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

/**
 * interface for anybody that wants to know about a stepping generator
 * 
 * @author ian
 * 
 */
public interface ISteppingListener
{
	public static interface IConstrainSpaceListener
	{

		/**
		 * bound the states is complete
		 * 
		 */
		void statesBounded(IBoundsManager boundsManager);

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

	public static interface IGenerateSolutionsListener
	{
		/**
		 * we've diced up the leg ends
		 * 
		 */
		void legsDiced();

		/**
		 * we've generated the routes
		 * 
		 */
		void legsGenerated();

		/**
		 * we've sorted out the leg scores
		 * 
		 */
		void legsScored();

		/**
		 * we have some solutions
		 * @param routes 
		 * 
		 */
		void solutionsReady(CompositeRoute[] routes);
	}

}
package com.planetmayo.debrief.satc.model.generator;

import java.util.Collection;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.ISteppingListener.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

/**
 * interface for how to run a stepping data generator
 * 
 * @author ian
 * 
 */
public interface IBoundsManager
{
	/**
	 * subscribe to contribution set events  
	 */		
	void addContributionsListener(IContributionsChangedListener newListener);
	
	/**
	 * unsubscribe from contribution set events  
	 */	
	void removeContributionsListener(IContributionsChangedListener newListener);
	
	/**
	 * subscribe to progress events 
	 */	
	void addBoundStatesListener(IConstrainSpaceListener newListener);

	/**
	 * unsubscribe from progress events 
	 */	
	void removeSteppingListener(IConstrainSpaceListener newListener);
	
	/**
	 * add contribution which will be used in constraint phase	
	 */
	void addContribution(BaseContribution contribution);

	/**
	 * remove contribution from constraint phase	
	 */	
	void removeContribution(BaseContribution contribution);
	
	/** 
	 * @return contributions which are used to contraint the problem space
	 */
	Collection<BaseContribution> getContributions();
	
	/**
	 * store the vehicle type, restart the process if started
	 * 
	 * @param v
	 *          the new vehicle type
	 */
	void setVehicleType(VehicleType v);	
	
	/**
	 * specify whether we should do a 'run' after each contribution change
	 * 
	 * @param checked
	 */
	void setLiveRunning(boolean checked);
	
	/**
	 * indicate whether we do 'run' after each contribution change
	 * 
	 * @return
	 */
	boolean isLiveEnabled();
	
	/**
	 * restart the set of contributions
	 * 
	 */
	void restart();
	
	/**
	 * clear problem space and remove all contributions
	 * 
	 */
	void clear();	

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
	 * @return constrained problem space on current space
	 */
	ProblemSpace getSpace();
	
	/**
	 * 
	 * @return is bounds manager already processed all contributions 
	 */
	boolean isCompleted();
	

	/** control what diagnostic data is broadcast during constrain problem space phase
	 * 
	 * @author ian
	 *
	 */
	public static interface IShowBoundProblemSpaceDiagnostics
	{
		/** broadcast all of the bounded states
		 * 
		 * @param onOff
		 */
		public void setShowAllBounds(boolean onOff);
		
		/** broadcast just the leg end states
		 * 
		 * @param onOff
		 */
		public void setShowLegEndBounds(boolean onOff);
	}

	/** control what diagnostic data is broadcast during generate solutions phase
	 * 
	 * @author ian
	 *
	 */
	public static interface IShowGenerateSolutionsDiagnostics
	{
		/** broadcast the list of generated grid points
		 * 
		 * @param routes
		 */
		public void setShowPoints(boolean onOff);
		
		/** broadcast the list of achievable grid points
		 * 
		 * @param routes
		 */
		public void setShowAchievablePoints(boolean onOff);
		
		/** show the achievable routes inside legs
		 * 
		 * @param routes
		 */
		public void setShowRoutes(boolean onOff);
		
		/** show the processed routes, including scores
		 * 
		 * @param routes
		 */
		public void setShowRoutesWithScores(boolean onOff);
	}
}

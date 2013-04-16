package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.ProblemSpaceView;

public interface ISolver
{
	/**
	 * returns contribution repository associated with solver 
	 */
	IContributions getContributions();
	
	/**
	 * returns bounds manager 
	 */
	IBoundsManager getBoundsManager();
	
	ISolutionGenerator getSolutionGenerator();
	
	ProblemSpaceView getProblemSpace();
	
	void clear();
	
	void run();
	
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
	
	boolean isAutoGenerateSolutions();

  void setAutoGenerateSolutions(boolean autoGenerateSolutions);
  
  void setVehicleType(VehicleType type);
  
  VehicleType getVehicleType();
}

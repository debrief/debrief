package com.planetmayo.debrief.satc.model.generator;

import java.util.List;

import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;

public interface IGenerateSolutionsListener
{

	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	void legsScored(List<CoreLeg> theLegs);

	/**
	 * we have some solutions
	 * @param routes 
	 * 
	 */
	void solutionsReady(CompositeRoute[] routes);
	
	/** we're about to start generating solutions
	 * 
	 */
	void startingGeneration();
	
	/**
	 * solution generator finished generation. 
	 * 
	 * If generation job finished successfully it will be called 
	 * after solutionsReady. 
	 * 
	 * If generation job was canceled or finished with error
	 * this method will be called after jobManager closes 
	 * all job resources   
	 */
	void finishedGeneration(Throwable error);
}
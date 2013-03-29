package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;

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
	void legsScored(ArrayList<CoreLeg> theLegs);

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
}
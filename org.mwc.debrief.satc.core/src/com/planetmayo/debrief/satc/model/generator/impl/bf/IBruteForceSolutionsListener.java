package com.planetmayo.debrief.satc.model.generator.impl.bf;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;

public interface IBruteForceSolutionsListener extends IGenerateSolutionsListener
{
	
	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	void legsScored(List<LegWithRoutes> theLegs);	
}

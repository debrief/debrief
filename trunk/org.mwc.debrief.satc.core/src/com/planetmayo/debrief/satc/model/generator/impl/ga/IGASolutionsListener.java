package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;

public interface IGASolutionsListener extends IGenerateSolutionsListener
{
	
	/** learn that an iteration is complete
	 * 
	 * @param topRoutes the top performing routes for this iteration
	 * @param topScore the score of the single best performing route
	 */
	void iterationComputed(List<CompositeRoute> topRoutes, double topScore);
}

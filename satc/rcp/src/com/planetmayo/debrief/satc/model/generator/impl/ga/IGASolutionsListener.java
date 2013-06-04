package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;

public interface IGASolutionsListener extends IGenerateSolutionsListener
{
	
	void iterationComputed(List<CompositeRoute> topRoutes);
}

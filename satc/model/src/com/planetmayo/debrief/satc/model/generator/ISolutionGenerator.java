package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.Precision;

public interface ISolutionGenerator
{

	public abstract void addReadyListener(IGenerateSolutionsListener listener);

	public abstract void removeReadyListener(IGenerateSolutionsListener listener);
	
	public void setPrecision(Precision precision);

}
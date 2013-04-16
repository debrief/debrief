package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.Precision;

public interface ISolutionGenerator
{

	/** someone's interested in solution generation
	 * 
	 * @param listener
	 */
	void addReadyListener(IGenerateSolutionsListener listener);

	/** someone's no longer interested in solution generation
	 * 
	 * @param listener
	 */
	void removeReadyListener(IGenerateSolutionsListener listener);
	
	/** specify how detailed the maths should be
	 * 
	 * @param precision
	 */
	void setPrecision(Precision precision);
	
	void clear();
	
	void generateSolutions();
}
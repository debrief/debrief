package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.Precision;

public interface ISolutionGenerator
{

	/** someone's interested in solution generation
	 * 
	 * @param listener
	 */
	public abstract void addReadyListener(IGenerateSolutionsListener listener);

	/** someone's no longer interested in solution generation
	 * 
	 * @param listener
	 */
	public abstract void removeReadyListener(IGenerateSolutionsListener listener);
	
	/** specify how detailed the maths should be
	 * 
	 * @param precision
	 */
	public void setPrecision(Precision precision);

	
	/** cancel the solution generation process, if it's running
	 * 
	 */
	public void interruptGeneration();


}
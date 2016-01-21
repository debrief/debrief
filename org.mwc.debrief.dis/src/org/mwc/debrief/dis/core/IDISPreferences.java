package org.mwc.debrief.dis.core;

public interface IDISPreferences
{
	/** whether the current plot should be re-used when a snenario starts/restarts
	 * 
	 * @return yes/no
	 */
	public boolean reusePlot();
	
	/** path to the current scenario input file
	 * 
	 * @return
	 */
	public String inputFile();
}

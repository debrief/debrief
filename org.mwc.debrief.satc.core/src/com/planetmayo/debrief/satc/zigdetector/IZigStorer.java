package com.planetmayo.debrief.satc.zigdetector;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

/** interface for listener class that is told when a new leg is detected
 * 
 * @author ian
 *
 */
public interface IZigStorer
{

	/**
	 * register this leg of data
	 * 
	 * @param scenario
	 * @param tStart
	 * @param tEnd
	 * @param sensor - optional sensor object that produced the data
	 * @param rms - the %age error from the RMS for the whole leg
	 */
	void storeZig(String scenarioName, long tStart, long tEnd, Sensor sensor,
			double rms);
	
	public ArrayList<StraightLegForecastContribution> getSlices();

	/** the algorithm will probably have to produce once final straight leg, from the last
	 * detected zig until the end of the data
	 * 
	 */
	void finish();


}
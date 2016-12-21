package org.mwc.debrief.track_shift.zig_detector.target;


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
	 * @param rms - the %age error from the RMS for the whole leg
	 */
	void storeZig(String scenarioName, long tStart, long tEnd, double rms);
	
	/** the algorithm will probably have to produce once final straight leg, from the last
	 * detected zig until the end of the data
	 * 
	 */
	void finish();


}
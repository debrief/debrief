package org.mwc.asset.scenariocontroller.preferences;

import MWC.GenericData.*;

/** interface for object that stores time preferences
 * 
 * @author ian.mayo
 *
 */
public interface TimeControllerPreferences
{
	/** set how the time is to be displayed
	 * 
	 * @param format
	 */
	public void setDTGFormat(String format);
	
	/** determine how the time is to be displayed
	 * 
	 * @return
	 */
	public String getDTGFormat();
	
	/** find out the small step size
	 * 
	 * @return
	 */
	public Duration getSmallStep();
	
	/** set the small step size
	 * 
	 * @param step
	 */
	public void setSmallStep(Duration step);
	
	/** get the time interval for auto-stepping
	 * 
	 */
	public Duration getAutoInterval();
	
	/** set the time interval for auto-stepping
	 * 
	 */
	public void setAutoInterval(Duration duration);
	

}

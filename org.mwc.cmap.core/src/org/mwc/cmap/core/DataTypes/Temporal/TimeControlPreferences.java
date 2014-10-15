/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.DataTypes.Temporal;

import MWC.GenericData.*;

/** interface for object that stores time preferences
 * 
 * @author ian.mayo
 *
 */
public interface TimeControlPreferences
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
	
	/** find out the large step size
	 * 
	 * @return
	 */
	public Duration getLargeStep();
	
	/** set the small step size
	 * 
	 * @param step
	 */
	public void setSmallStep(Duration step);
	
	/** set the large step size
	 * 
	 * @param step
	 */
	public void setLargeStep(Duration step);
	
	/** get the time interval for auto-stepping
	 * 
	 */
	public Duration getAutoInterval();
	
	/** set the time interval for auto-stepping
	 * 
	 */
	public void setAutoInterval(Duration duration);
	
	/** get the slider start time (not necessarily the same as the time period for the data)
	 * 
	 */
	public HiResDate getSliderStartTime();

	/** get the slider end time (not necessarily the same as the time period for the data)
	 * 
	 */
	public HiResDate getSliderEndTime();
	
	/** set the slider start time (not necessarily the same as the time period for the data)
	 * 
	 */
	public void setSliderStartTime(HiResDate dtg);
	
	/** set the slider end time (not necessarily the same as the time period for the data)
	 * 
	 */
	public void setSliderEndTime(HiResDate dtg);

}

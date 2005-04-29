/**
 * 
 */
package org.mwc.cmap.core.DataTypes.Temporal;

import java.beans.PropertyChangeListener;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

/**
 * Interface describing time-related data. It provides functions to both
 * retrieve and modify the current time.
 * @author ian.mayo
 */
public interface TemporalDataset
{

	/**
	 * obtain the time period covered by the data
	 * @return the time period
	 */
	public TimePeriod getPeriod();

	/**
	 * obtain the current time
	 * @return now
	 */
	public HiResDate getTime();

	/**
	 * modify the current time
	 */
	public void setTime(HiResDate dtg);
	
	/** listen out for the current time
	 * 
	 */
	public void addTimeChangeListener(PropertyChangeListener listener);
	
	/** stop listening out for the current time
	 * 
	 */
	public void removeTimeChangeListener(PropertyChangeListener listener);
	
	
}

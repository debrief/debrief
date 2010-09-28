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
public interface TimeProvider
{

	/** property name for the time being changed
	 * 
	 */
	public static final String TIME_CHANGED_PROPERTY_NAME = "TIME_CHANGED";
	
	
	/** propery name for the time-period being changed
	 * 
	 */
	public static final String PERIOD_CHANGED_PROPERTY_NAME = "PERIOD_CHANGED";

	
	/**
	 * obtain the time period covered by the data
	 * @return the time period
	 */
	public TimePeriod getPeriod();
	
	/** identifier for this time provider. We create this so that, when restored,  an xy plot 
	 * can loop through the open plots to determine which time provider to connect to
	 * 
	 */
	public String getId();

	/**
	 * obtain the current time
	 * @return now
	 */
	public HiResDate getTime();
	
	/** let somebody start listening to our changes
	 * 
	 * @param listener the new listener
	 * @param propertyType the (optional) property to listen to. Use null if you don't mind
	 */
	public void addListener(PropertyChangeListener listener, String propertyType);

	/** let somebody stop listening to our changes
	 * 
	 * @param listener the old listener
	 * @param propertyType the (optional) property to stop listening to. Use null if you don't mind
	 */
	public void removeListener(PropertyChangeListener listener, String propertyType);
	
}

package org.mwc.cmap.core.DataTypes.Temporal;

import java.util.Vector;

import MWC.GUI.Layer;
import MWC.GenericData.TimePeriod;

/**
 * Interface for objects for whom a 'valid period' of time can be specified
 * 
 * @author ian.mayo
 *
 */

public interface ControllablePeriod
{


	public static final String FILTER_TO_TIME_PERIOD = "Filter to time period";

	/** indicate that a period has been selected
	 * 
	 * @param period
	 */
	public void setPeriod(TimePeriod period);
	
	/** indicate that an operation has been requested on the selected period
	 * 
	 */
	public Vector<Layer> performOperation(AnOperation operation);
	
	/** indicate that an operation has been requested on the selected period
	 * 
	 */
	public Vector<Layer> performOperation(String operationName);
	
	
	/** retrieve the list of available operations
	 * 
	 */
	public Vector<AnOperation> getOperations();
	
	/** find out what the current period is
	 * 
	 */
	public TimePeriod getPeriod();
	
	public static interface AnOperation
	{
		/**
		 * find out the name of this operation
		 * 
		 * @return
		 */
		public String getName();
		
		/**
		 * get a description for this operation
		 * 
		 * @return
		 */
		public String getDescription();
		
		/**
		 * perform this operation to the indicated objects over the indicated period
		 * 
		 * @param period
		 * @param selection
		 * @return series of layers to be updated
		 */
		public Vector<Layer> apply(TimePeriod period, 
												Object[] selection);
	}	
}

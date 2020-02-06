
package Debrief.Wrappers.Track;

import MWC.GenericData.HiResDate;

/** interface for class that is able to provide the an additional
 * value for specified time stams to help with styling, plus an accessor
 * to find out if that styling should be applied
 * 
 * @author ian
 *
 */
public interface ITimeVariableProvider
{
	
	/** what is the value at the specified time
	 * 
	 * @param dtg
	 * @return
	 */
	long getValueAt(HiResDate dtg);

	/** whether the user wants the errors to be drawn to scale on the spatial plot
	 * 
	 * @return
	 */
	boolean applyStyling();
}

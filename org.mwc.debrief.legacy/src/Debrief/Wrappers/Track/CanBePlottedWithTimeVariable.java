package Debrief.Wrappers.Track;

import MWC.GUI.CanvasType;

/** interface for classes that wish to use a residual error to assist with their plotting
 * 
 * @author ian
 *
 */
public interface CanBePlottedWithTimeVariable
{

	/** use the specified error provider to customise how the item gets painted
	 * 
	 * @param dest
	 * @param errorProvider
	 */
	void paint(CanvasType dest, ITimeVariableProvider errorProvider);

}

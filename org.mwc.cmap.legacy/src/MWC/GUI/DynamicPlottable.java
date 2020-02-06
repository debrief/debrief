
package MWC.GUI;

/** interface for time-sensitive objects that need to know the time in order to plot
 * 
 * @author ian
 *
 */
public interface DynamicPlottable
{

	/** paint this object to the specified canvas
	 * @param dest current destination
	 * @param time milliseconds
   */
  public void paint(CanvasType dest, long time);
}

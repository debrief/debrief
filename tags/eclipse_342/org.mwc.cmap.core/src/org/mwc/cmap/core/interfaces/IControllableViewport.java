/**
 * 
 */
package org.mwc.cmap.core.interfaces;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.WorldArea;

/** interface for objects who have a 2d geographic view that can be
 * externally controlled
 * @author ian.mayo
 *
 */
public interface IControllableViewport
{

	
	/** control the current coverage of the view
	 * 
	 * @param target
	 */
	public void setViewport(WorldArea target);
	
	/** find out the current coverage of the view
	 * 
	 * @return
	 */
	public WorldArea getViewport();

	/** control the complete projection details
	 * 
	 * @param proj the new projection to use
	 */
	public void setProjection(PlainProjection proj);
	
	/** find out the full projection details
	 * 
	 */
	public PlainProjection getProjection();
	
	/** get it to redraw itself
	 * 
	 */
	public void update();
	
	/** and to fit to window
	 * 
	 */
	public void rescale();
}

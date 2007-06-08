package org.mwc.debrief.core.editors.painters;

import MWC.GUI.*;
import MWC.GenericData.HiResDate;

/** interface for any classes which want to be able to paint layers possibly in a special way, such as 
 * our snail/normal painters
 * 
 * @author ian.mayo
 *
 */
public interface TemporalLayerPainter extends Editable
{
	/** ok, get painting
	 * 
	 * @param theLayer
	 * @param dest
	 * @param dtg
	 */
	public void paintThisLayer(Layer theLayer, CanvasType dest, HiResDate dtg);
	
	/** retrieve it's name
	 * 
	 * @return the name of this painter
	 */
	public String getName();
}
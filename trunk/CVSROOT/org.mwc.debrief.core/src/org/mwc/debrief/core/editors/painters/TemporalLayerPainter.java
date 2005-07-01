package org.mwc.debrief.core.editors.painters;

import MWC.GUI.*;
import MWC.GenericData.HiResDate;

/** interface for any classes which want to be able to paint layers possibly in a special way, such as 
 * our snail/normal painters
 * 
 * @author ian.mayo
 *
 */
public interface TemporalLayerPainter
{
	public void paintThisLayer(Layer theLayer, CanvasType dest, HiResDate dtg);
}
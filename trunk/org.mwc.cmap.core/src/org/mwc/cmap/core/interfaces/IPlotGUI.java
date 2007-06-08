package org.mwc.cmap.core.interfaces;

import java.awt.Color;

public interface IPlotGUI
{
	/** find out the background color
	 * 
	 * @return the current background color of the plot
	 */
	public Color getBackgroundColor();
	
	/** set the background color
	 * 
	 * @param theColor the background color
	 */
	public void setBackgroundColor(Color theColor);
}

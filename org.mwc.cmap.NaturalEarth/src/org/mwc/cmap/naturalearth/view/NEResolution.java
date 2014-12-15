package org.mwc.cmap.naturalearth.view;

import java.util.ArrayList;

public class NEResolution extends ArrayList<NEFeatureStyle>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** whether this set of styles is suited to plotting this particular scale
	 * 
	 * @param scale
	 * @return
	 */
	public boolean canPlot(double scale)
	{
		return true;
	}

}

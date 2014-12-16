package org.mwc.cmap.naturalearth.view;

import java.util.ArrayList;
import java.util.Iterator;

public class NEFeatureSet extends ArrayList<NEResolution>
{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NEResolution resolutionFor(double scale)
	{
		Iterator<NEResolution> iter = super.iterator();
		while (iter.hasNext())
		{
			NEResolution thisR = (NEResolution) iter.next();
			if(thisR.canPlot(scale))
			{
				return thisR;
			}
		}
		
		return null;
	}
}

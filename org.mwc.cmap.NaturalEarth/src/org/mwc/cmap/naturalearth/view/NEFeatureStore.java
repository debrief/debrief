package org.mwc.cmap.naturalearth.view;

import java.util.Enumeration;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.naturalearth.Activator;

import MWC.GUI.Editable;
import MWC.GUI.Plottables;

public class NEFeatureStore extends Plottables
{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NEResolution resolutionFor(double scale)
	{
		Enumeration<Editable> iter = super.elements();
		while (iter.hasMoreElements())
		{
			NEResolution thisR = (NEResolution) iter.nextElement();
			if(thisR.getVisible())
			{
				if(thisR.canPlot(scale))
				{
					return thisR;
				}
			}
		}
		
		return null;
	}

	@Override
	public void add(Editable thePlottable)
	{
		if(!(thePlottable instanceof NEResolution))
		{
			Activator.logError(Status.WARNING, "Should not be adding this to a NE Feature:" + thePlottable, null);
		} else {
			super.add(thePlottable);
			((NEResolution)thePlottable).setParent(this);
		}
	}
	
}

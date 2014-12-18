package org.mwc.cmap.naturalearth.view;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.naturalearth.Activator;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;

public class NEResolution extends BaseLayer 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final private Double _minS;
	final private Double _maxS;
	final private String _name;

	private boolean _activeRes;

	public NEResolution(String name, Double minS, Double maxS)
	{
		_name = name;
		_minS = minS;
		_maxS = maxS;
	}
	
	
	
	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}



	/** whether this set of styles is suited to plotting this particular scale
	 * 
	 * @param scale
	 * @return
	 */
	public boolean canPlot(double scale)
	{
		boolean valid = true;
		if(_minS != null)
		{
			valid = scale > _minS;
		}
		if(valid)
		{
			if(_maxS != null)
				valid = scale <= _maxS;
		}
		
		return valid;
	}

	public String getName()
	{
		final String res;
		
		// do different formatting if it's the active res
		if(_activeRes)
		{
			res = "[" + _name + "]";
		}
		else
			res = _name;
		return res;
	}

	
	@Override
	public void add(Editable thePlottable)
	{
		if(!(thePlottable instanceof NEFeatureStyle))
		{
			Activator.logError(Status.WARNING, "Should not be adding this to a NE Feature:" + thePlottable, null);
		}
		super.add(thePlottable);
	}

	/** indicate that this is the currently active resolution
	 * 
	 * @param b
	 */
	public void setActive(boolean b)
	{
		_activeRes = b;
	}
	
	
}

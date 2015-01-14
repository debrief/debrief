package org.mwc.cmap.naturalearth.view;

import MWC.GUI.Plottable;

public class NEResolution extends NEFeatureGroup
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Double _minS;
	private Double _maxS;
	boolean _activeRes;

	public NEResolution(String name)
	{
		this(null, name, null, null);
	}
	
	public NEResolution(NEFeatureStore featureSet, String name, Double minS, Double maxS)
	{
		super(featureSet, name);
		_minS = minS;
		_maxS = maxS;
	}
	
	public Double getMinScale()
	{
		return _minS;
	}
	
	public Double getMaxScale()
	{
		return _maxS;
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

	/** indicate that this is the currently active resolution
	 * 
	 * @param b
	 */
	public void setActive(boolean b)
	{
		_activeRes = b;
	}

	/**
	 * Sort the resolution objects by scale (to prevent them being sorted by name)
	 */
	public final int compareTo(final Plottable o)
	{
		final int res;
		
		final NEResolution other = (NEResolution) o;
		
		// do we have a max value?
		if(_maxS == null)
		{
			// nope, so we're the fallback method - make us last
			res = 1;
		}
		else
		{
			// does he have a max value?
			if(other._maxS == null)
			{
				// he's the fallback method, make him last
				res = -1;
			}
			else
			{
				res = _maxS.compareTo(other._maxS);
			}
		}
		
		return res;
	}

	public void setMinScale(Double minS)
	{
		_minS = minS;
	}
	
	public void setMaxScale(Double maxS)
	{
		_maxS = maxS;
	}	
	
}

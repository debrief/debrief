package org.mwc.cmap.naturalearth.view;




public class NEResolution extends NEFeatureGroup
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Double _minS;
	private Double _maxS;
	boolean _activeRes;

	public NEResolution(String name, Double minS, Double maxS)
	{
		this(name);
		_minS = minS;
		_maxS = maxS;
	}
	
	public NEResolution(String name)
	{
		super(name);
	}
	
	
	
	public Double getMin()
	{
		return _minS;
	}

	public void setMin(Double minS)
	{
		this._minS = minS;
	}

	public Double getMax()
	{
		return _maxS;
	}

	public void setMax(Double maxS)
	{
		this._maxS = maxS;
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
	
	
}

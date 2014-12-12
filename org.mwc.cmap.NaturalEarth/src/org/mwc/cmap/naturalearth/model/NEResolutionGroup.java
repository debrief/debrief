package org.mwc.cmap.naturalearth.model;

import java.util.ArrayList;


public class NEResolutionGroup extends ArrayList<NEFeature>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double _minScale = null;
	private Double _maxScale = null;
	
	public NEResolutionGroup(Double minS, Double maxS)
	{
		_minScale = minS;
		_maxScale = maxS;
	}
	
	/** whether this resolution is suited to the provided scale
	 * 
	 * @param scale
	 * @return
	 */
	public boolean canHandle(double scale)
	{
		boolean isGood = true;
		// is this scale within our bounds?
		if(_minScale != null)
		{
			isGood = scale > _minScale;
		}
		if(isGood)
		{
			if(_maxScale != null)
			{
				isGood = scale < _maxScale;
			}
		}
		
		return isGood;
	}
}
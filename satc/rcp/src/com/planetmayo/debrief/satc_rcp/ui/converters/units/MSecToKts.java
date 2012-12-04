package com.planetmayo.debrief.satc_rcp.ui.converters.units;

import com.planetmayo.debrief.satc.util.GeoSupport;

public class MSecToKts extends AbstractUnitConverter
{
	
	public MSecToKts() 
	{
		super(true);
	}
	
	@Override
	protected Double safeConvert(Number obj)
	{
		return GeoSupport.MSec2kts(obj.doubleValue());
	}
}

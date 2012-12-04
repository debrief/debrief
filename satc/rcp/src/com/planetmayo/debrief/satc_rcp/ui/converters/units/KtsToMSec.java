package com.planetmayo.debrief.satc_rcp.ui.converters.units;

import com.planetmayo.debrief.satc.util.GeoSupport;

public class KtsToMSec extends AbstractUnitConverter
{
	
	public KtsToMSec() 
	{
		super(false);
	}
	
	@Override
	protected Double safeConvert(Number obj)
	{
		return GeoSupport.kts2MSec(obj.doubleValue());
	}
}

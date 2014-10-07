package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class YdsToMeter extends AbstractUnitConverter
{
	public YdsToMeter()
	{
		super(false);
	}

	@Override
	public Double safeConvert(Number obj)
	{		
		return MWC.Algorithms.Conversions.ft2m(obj.doubleValue()*3);
	}	
}

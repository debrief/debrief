package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class YdsToMeter extends AbstractUnitConverter
{
  final static private double FT_M_CONV = 0.3048;

	public YdsToMeter()
	{
		super(false);
	}

	@Override
	public Double safeConvert(Number obj)
	{		
		return (obj.doubleValue()*3) * FT_M_CONV;
	}	
}

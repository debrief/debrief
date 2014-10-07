package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class MeterToYds extends AbstractUnitConverter
{
	public MeterToYds()
	{
		super(false);
	}

	@Override
	public Double safeConvert(Number obj)
	{		
		return MWC.Algorithms.Conversions.m2ft(obj.doubleValue())/3;
	}	
}

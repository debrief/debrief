package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class MeterToYds extends AbstractUnitConverter
{
  final static private double FT_M_CONV = 0.3048;

	public MeterToYds()
	{
		super(false);
	}

	@Override
	public Double safeConvert(Number obj)
	{		
		return (obj.doubleValue() / FT_M_CONV)/3;
	}	
}

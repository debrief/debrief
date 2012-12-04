package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class DegToRads extends AbstractUnitConverter
{
	public DegToRads()
	{
		super(false);
	}

	@Override
	protected Double safeConvert(Number obj)
	{		
		return Math.toRadians(obj.doubleValue());
	}	
}

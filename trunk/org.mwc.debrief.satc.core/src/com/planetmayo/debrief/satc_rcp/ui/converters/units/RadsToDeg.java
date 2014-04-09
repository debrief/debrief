package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class RadsToDeg extends AbstractUnitConverter
{
	
	public RadsToDeg()
	{
		super(true);
	}

	@Override
	protected Double safeConvert(Number obj)
	{
		return Math.toDegrees(obj.doubleValue());
	}
	
}

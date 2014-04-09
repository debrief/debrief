package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class ScaleConverter extends AbstractUnitConverter
{
	private final AbstractUnitConverter nested;
	private final double scale;
	
	public ScaleConverter(AbstractUnitConverter nested, double scale)
	{
		super(nested.isModelToUI);
		this.nested = nested;
		this.scale = scale;
	}

	@Override
	protected Double safeConvert(Number obj)
	{
		if (isModelToUI)
		{
			return nested.safeConvert(obj.doubleValue()) * scale;
		}
		else 
		{
			return nested.safeConvert(obj.doubleValue() * scale);
		}
	}
}

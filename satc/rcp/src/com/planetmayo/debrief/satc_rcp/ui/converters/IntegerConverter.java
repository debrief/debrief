package com.planetmayo.debrief.satc_rcp.ui.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerConverter implements IConverter
{

	@Override
	public Object convert(Object obj)
	{
		return ((Number) obj).intValue();
	}

	@Override
	public Object getFromType()
	{
		return Number.class;
	}

	@Override
	public Object getToType()
	{
		return Integer.class;
	}
}

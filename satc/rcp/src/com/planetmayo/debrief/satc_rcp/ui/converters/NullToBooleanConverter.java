package com.planetmayo.debrief.satc_rcp.ui.converters;

import org.eclipse.core.databinding.conversion.IConverter;

public class NullToBooleanConverter implements IConverter
{

	@Override
	public Object convert(Object from)
	{
		return from != null;
	}

	@Override
	public Object getFromType()
	{
		return Object.class;
	}

	@Override
	public Object getToType()
	{
		return Boolean.class;
	}
	
}

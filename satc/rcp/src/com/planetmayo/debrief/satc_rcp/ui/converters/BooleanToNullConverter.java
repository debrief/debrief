package com.planetmayo.debrief.satc_rcp.ui.converters;

import java.util.concurrent.Callable;

import org.eclipse.core.databinding.conversion.IConverter;

public class BooleanToNullConverter<T> implements IConverter
{
	private T defaultValue;
	private Class<T> computatorClass;
	private Callable<T> defaultValueComputator;
	
	public BooleanToNullConverter(T defaultValue) 
	{
		this.defaultValue = defaultValue;
	}
	
	public BooleanToNullConverter(Class<T> computatorClass, Callable<T> defaultValueComputator) 
	{
		this.computatorClass = computatorClass;
		this.defaultValueComputator = defaultValueComputator;
	}	
	
	@Override
	public Object convert(Object from)
	{
		Boolean data = (Boolean) from;
		if (data == Boolean.TRUE) 
		{
			if (defaultValue != null) 
			{
				return defaultValue;
			} else 
			{
				try 
				{
					return defaultValueComputator.call();
				} 
				catch (Exception e) 
				{
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	@Override
	public Object getFromType()
	{
		return Boolean.class;
	}

	@Override
	public Object getToType()
	{
		if (defaultValue != null) 
		{
			return defaultValue.getClass(); 
		}
		return computatorClass;
	}
}

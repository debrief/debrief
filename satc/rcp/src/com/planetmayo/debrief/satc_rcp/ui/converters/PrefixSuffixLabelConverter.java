package com.planetmayo.debrief.satc_rcp.ui.converters;

import org.eclipse.core.databinding.conversion.IConverter;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.AbstractUnitConverter;

public class PrefixSuffixLabelConverter implements IConverter, Cloneable
{
	private AbstractUnitConverter nestedUnitConverter;

	private final Class<?> fromType;
	private final String prefix;
	private final String suffix;

	public PrefixSuffixLabelConverter(Class<?> fromType)
	{
		this(fromType, "", "");
	}

	public PrefixSuffixLabelConverter(Class<?> fromType, String suffix)
	{
		this(fromType, "", suffix);
	}

	public PrefixSuffixLabelConverter(Class<?> fromType, String prefix,
			String suffix)
	{
		this.fromType = fromType;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public Object convert(Object from)
	{
		if (from == null) 
		{
			return "";
		}
		if (from instanceof Number)
		{
			if (getNestedUnitConverter() != null) {
				from = getNestedUnitConverter().convert(from);
			}			
			from = ((Number) from).intValue();
		}
		return prefix + from + suffix;
	}
	
	public AbstractUnitConverter getNestedUnitConverter()
	{
		return nestedUnitConverter;
	}

	public void setNestedUnitConverter(AbstractUnitConverter nestedUnitConverter)
	{
		this.nestedUnitConverter = nestedUnitConverter;
	}

	@Override
	public Object getFromType()
	{
		return fromType;
	}

	@Override
	public Object getToType()
	{
		return String.class;
	}
	
	@Override
	public PrefixSuffixLabelConverter clone() throws CloneNotSupportedException
	{
		return (PrefixSuffixLabelConverter) super.clone();
	}	
}

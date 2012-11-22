package com.planetmayo.debrief.satc_rcp.ui;

import org.eclipse.core.databinding.conversion.IConverter;

public class PrefixSuffixLabelConverter implements IConverter
{

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
		if (from == null) {
			return "";
		}
		if (from instanceof Number)
		{
			from = ((Number) from).intValue();
		}
		return prefix + from + suffix;
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
}

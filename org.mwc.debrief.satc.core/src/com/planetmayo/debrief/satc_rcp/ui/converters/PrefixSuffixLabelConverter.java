/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui.converters;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.core.databinding.conversion.IConverter;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.AbstractUnitConverter;

public class PrefixSuffixLabelConverter implements IConverter, Cloneable
{
	private AbstractUnitConverter nestedUnitConverter;

	private final NumberFormat numberFormat;
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
		this(fromType, prefix, suffix, new DecimalFormat("0"));
	}
	
	public PrefixSuffixLabelConverter(Class<?> fromType, String prefix,
			String suffix, NumberFormat numberFormat)
	{
		this.fromType = fromType;
		this.prefix = prefix;
		this.suffix = suffix;
		this.numberFormat = numberFormat;
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
			from = numberFormat.format((Number) from);
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

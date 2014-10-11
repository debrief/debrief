/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui.converters.units;

import org.eclipse.core.databinding.conversion.IConverter;

public abstract class AbstractUnitConverter implements IConverter
{	
	protected boolean isModelToUI;
	
	public AbstractUnitConverter(boolean isModelToUI) 
	{
		this.isModelToUI = isModelToUI;
	}
	
	protected abstract Double safeConvert(Number obj);

	@Override
	public Object convert(Object obj)
	{
		if (obj == null) {
			return null;
		}		
		Double result = safeConvert((Number) obj);
		return result;
	}

	@Override
	public Object getFromType()
	{
		return Number.class;
	}

	@Override
	public Object getToType()
	{
		return Double.class;
	}	
}

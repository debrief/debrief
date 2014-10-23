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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc_rcp.ui.converters.units;

import com.planetmayo.debrief.satc.util.GeoSupport;

public class MSecToKts extends AbstractUnitConverter
{
	
	public MSecToKts() 
	{
		super(true);
	}
	
	@Override
	protected Double safeConvert(Number obj)
	{
		return GeoSupport.MSec2kts(obj.doubleValue());
	}
}

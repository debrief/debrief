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
package com.planetmayo.debrief.satc.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.planetmayo.debrief.satc.util.ObjectUtils;

public class ModelTestBase
{
	protected static final double EPS = 0.000001d;
	
	protected Date parseDate(String pattern, String data) 
	{
		return ObjectUtils.safeParseDate(new SimpleDateFormat(pattern), data);
	}
}

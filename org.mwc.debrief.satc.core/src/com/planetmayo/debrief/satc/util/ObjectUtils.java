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
package com.planetmayo.debrief.satc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ObjectUtils
{
	
	public static <T> T safe(T object, T def) 
	{
		return object == null ? def : object;
	}
	
	public static boolean safeEquals(Object obj1, Object obj2) 
	{
		if (obj1 == null && obj2 == null) 
		{
			return true;
		}
		if (obj1 == null || obj2 == null) 
		{
			return false;
		}
		return obj1.equals(obj2);
	}
	
	public static Date safeParseDate(DateFormat dateFormat, String date) 
	{
		try 
		{
			return dateFormat.parse(date);
		}
		catch (ParseException ex)
		{
			return null;
		}
	}
}

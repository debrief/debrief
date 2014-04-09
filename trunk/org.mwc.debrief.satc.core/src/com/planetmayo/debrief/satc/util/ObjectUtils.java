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

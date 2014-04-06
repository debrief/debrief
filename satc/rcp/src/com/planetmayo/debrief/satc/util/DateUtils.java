package com.planetmayo.debrief.satc.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
	
	public static Date date(int year, int month, int day, int hour, int minute, int second) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Date date(int year, int month, int day) 
	{
		return date(year, month, day, 0, 0, 0);
	}
}

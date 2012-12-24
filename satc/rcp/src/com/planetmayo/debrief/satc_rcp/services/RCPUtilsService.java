package com.planetmayo.debrief.satc_rcp.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.support.UtilsService;

public class RCPUtilsService implements UtilsService
{

	@Override
	public String formatDate(String pattern, Date date)
	{
		return new SimpleDateFormat(pattern).format(date);
	}

	@Override
	public Date parseDate(String pattern, String text)
	{
		try
		{
			return new SimpleDateFormat(pattern).parse(text);
		}
		catch (ParseException ex)
		{
			SupportServices.INSTANCE.getLog().warn("Can't parse date", ex);
			return null;
		}
	}

	@Override
	public <T> T higherElement(TreeSet<T> set, T currentElement)
	{
		if (set == null || set.isEmpty())
		{
			return null;
		}
		return currentElement == null ? set.first() : set.higher(currentElement);
	}
}

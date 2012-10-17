package com.planetmayo.debrief.satc_rcp.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.planetmayo.debrief.satc.support.ConverterService;
import com.planetmayo.debrief.satc.support.SupportServices;

public class RCPConverterService implements ConverterService
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

}

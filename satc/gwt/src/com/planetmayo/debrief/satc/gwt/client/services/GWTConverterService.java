package com.planetmayo.debrief.satc.gwt.client.services;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.planetmayo.debrief.satc.support.ConverterService;

public class GWTConverterService implements ConverterService
{

	@Override
	public String formatDate(String pattern, Date date)
	{
		return DateTimeFormat.getFormat(pattern).format(date);
	}

	@Override
	public Date parseDate(String pattern, String text)
	{
		return DateTimeFormat.getFormat(pattern).parse(text);
	}

}

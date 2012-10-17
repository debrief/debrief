package com.planetmayo.debrief.satc.support;

import java.util.Date;

public interface ConverterService
{

	String formatDate(String pattern, Date date);

	Date parseDate(String pattern, String text);
}

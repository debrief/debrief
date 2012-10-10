package com.planetmayo.debrief.satc.support;

import java.util.Date;

public interface ConverterService
{

	 Date parseDate(String pattern, String text);
	 
	 String formatDate(String pattern, Date date);
}

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

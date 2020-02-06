
package com.planetmayo.debrief.satc.model;

import java.util.Date;

import com.planetmayo.debrief.satc.util.ObjectUtils;

import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ModelTestBase
{
	protected static final double EPS = 0.000001d;
	
	protected Date parseDate(String pattern, String data) 
	{
		return ObjectUtils.safeParseDate(new GMTDateFormat(pattern), data);
	}
}

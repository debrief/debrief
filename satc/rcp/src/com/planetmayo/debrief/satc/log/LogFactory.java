package com.planetmayo.debrief.satc.log;


public class LogFactory
{
	private static final LogService INSTANCE = new RCPLogService(); 
	
	public static LogService getLog() 
	{
		return INSTANCE;
	}
}

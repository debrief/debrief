package com.planetmayo.debrief.satc.support;

import java.util.Date;

public class SupportServices
{
	public static final SupportServices INSTANCE = new SupportServices();
	
	private volatile boolean initialized = false;
	private volatile LogService logService;
	private volatile ConverterService converterService;
	
	public synchronized void initialize(LogService logService, ConverterService converterService) 
	{
		this.logService = logService;
		this.converterService = converterService;
		initialized = true;
	}
	
	public LogService getLog() 
	{
		if (! initialized) 
		{
			throw new IllegalStateException("Support services isn't initialized. Do you forget to call " +
					"SupportServices.initialize in your RCP activator or GWT entry point?");
		}
		return logService;
	}

	public ConverterService getConverterService()
	{
		if (! initialized) 
		{
			throw new IllegalStateException("Support services isn't initialized. Do you forget to call " +
					"SupportServices.initialize in your RCP activator or GWT entry point?");
		}		
		return converterService;
	}
	
	public Date parseDate(String pattern, String text) 
	{
		return getConverterService().parseDate(pattern, text); 
	}
	
	public String formatDate(String pattern, Date date) 
	{
		return getConverterService().formatDate(pattern, date); 
	}
}

package com.planetmayo.debrief.satc.support;

import java.util.Date;

public class SupportServices
{
	public static final SupportServices INSTANCE = new SupportServices();

	private volatile boolean initialized = false;
	private volatile LogService logService;
	private volatile UtilsService utilsService;
	private volatile IOService ioService;

	public String formatDate(String pattern, Date date)
	{
		return getUtilsService().formatDate(pattern, date);
	}

	public UtilsService getUtilsService()
	{
		if (!initialized)
		{
			throw new IllegalStateException(
					"Support services isn't initialized. Do you forget to call "
							+ "SupportServices.initialize in your RCP activator or GWT entry point?");
		}
		return utilsService;
	}

	public IOService getIOService()
	{
		if (!initialized)
		{
			throw new IllegalStateException(
					"Support services isn't initialized. Do you forget to call "
							+ "SupportServices.initialize in your RCP activator or GWT entry point?");
		}
		return ioService;
	}

	public LogService getLog()
	{
		if (!initialized)
		{
			throw new IllegalStateException(
					"Support services isn't initialized. Do you forget to call "
							+ "SupportServices.initialize in your RCP activator or GWT entry point?");
		}
		return logService;
	}

	public synchronized void initialize(LogService logService,
			UtilsService converterService, IOService ioService)
	{
		this.logService = logService;
		this.utilsService = converterService;
		this.ioService = ioService;
		initialized = true;
	}

	public Date parseDate(String pattern, String text)
	{
		return getUtilsService().parseDate(pattern, text);
	}
}
